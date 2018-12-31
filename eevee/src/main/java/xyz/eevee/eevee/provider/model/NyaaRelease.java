package xyz.eevee.eevee.provider.model;

import common.util.TimeUtil;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.log4j.Log4j2;
import xyz.eevee.eevee.rss.model.NyaaReleaseItem;
import xyz.eevee.eevee.session.Session;

import java.time.Instant;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Builder
@Log4j2
@Value
public class NyaaRelease {
    private static final Pattern DETAIL_EXTRACTION_PATTERN = Pattern.compile(
        Session.getSession()
               .getConfiguration()
               .readString("eevee.animeReleaseDetailExtractionRegex")
    );

    @NonNull
    private String subber;
    @NonNull
    private String title;
    private int episode;
    private String quality;
    @NonNull
    private String format;
    @NonNull
    private String link;
    @NonNull
    private Instant pubDate;

    public static Optional<NyaaRelease> fromReleaseItem(NyaaReleaseItem releaseItem) {
        Matcher detailMatcher = DETAIL_EXTRACTION_PATTERN.matcher(releaseItem.getTitle());

        if (!detailMatcher.matches()) {
            log.error(String.format("No match found for string: %s.", releaseItem.getTitle()));
            return Optional.empty();
        }

        return Optional.of(NyaaRelease.builder()
                                      .subber(detailMatcher.group(1))
                                      .title(detailMatcher.group(2))
                                      .episode(Integer.parseInt(detailMatcher.group(3)))
                                      .quality(detailMatcher.group(6))
                                      .format(detailMatcher.group(10).toUpperCase(Locale.ENGLISH))
                                      .link(releaseItem.getLink())
                                      .pubDate(TimeUtil.rfc822ToInstant(releaseItem.getPubDate()))
                                      .build());
    }

    public String toString() {
        return String.format(
            "[%s] %s [Ep. %s | %s | %s]", subber, title, episode, quality, format
        );
    }
}
