package xyz.eevee.eevee.provider.model;

import common.util.TimeUtil;
import lombok.Builder;
import lombok.Data;
import xyz.eevee.eevee.rss.model.MangaDexReleaseItem;
import xyz.eevee.eevee.session.Session;

import java.time.Instant;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Builder
@Data
public class MangaDexRelease {
    private String title;
    private String chapter;
    private String scanlator;
    private String language;
    private String link;
    private Instant pubDate;

    public static Optional<MangaDexRelease> fromReleaseItem(MangaDexReleaseItem releaseItem) {
        final Pattern DETAIL_EXTRACTION_PATTERN = Pattern.compile(
                Session.getSession()
                       .getConfiguration()
                       .readString("eevee.mangaReleaseDetailExtractionRegex")
        );
        final Pattern META_EXTRACTION_PATTERN = Pattern.compile(
                Session.getSession()
                       .getConfiguration()
                       .readString("eevee.mangaReleaseMetaExtractionRegex")
        );
        final Matcher detailMatcher = DETAIL_EXTRACTION_PATTERN.matcher(releaseItem.getTitle());
        final Matcher metaMatcher = META_EXTRACTION_PATTERN.matcher(releaseItem.getDescription());

        if (!detailMatcher.matches() || !metaMatcher.matches()) {
            return Optional.empty();
        }

        return Optional.of(
                MangaDexRelease.builder()
                               .title(detailMatcher.group(1))
                               .chapter(detailMatcher.group(2))
                               .scanlator(metaMatcher.group(1))
                               .language(metaMatcher.group(3))
                               .link(releaseItem.getLink())
                               .pubDate(TimeUtil.rfc822ToInstant(releaseItem.getPubDate()))
                               .build()
        );
    }
}
