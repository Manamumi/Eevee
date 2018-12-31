package xyz.eevee.eevee.provider.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import xyz.eevee.eevee.session.Session;

import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Builder
public class HsRelease {
    @Getter
    @NonNull
    private HsReleaseData releaseData;
    @Getter
    @NonNull
    private String magnetLink;

    @Builder
    @Log4j2
    public static class HsReleaseData {
        private static final Pattern DETAIL_EXTRACTION_PATTERN = Pattern.compile(
            Session.getSession()
                   .getConfiguration()
                   .readString("eevee.animeReleaseDetailExtractionRegex")
        );
        private static Matcher detailMatcher;

        @Getter
        @NonNull
        private String subber;
        @Getter
        @NonNull
        private String title;
        @Getter
        private int episode;
        @Getter
        @NonNull
        private String quality;
        @Getter
        @NonNull
        private String format;

        public static Optional<HsReleaseData> fromString(String str) {
            if (detailMatcher == null) {
                detailMatcher = DETAIL_EXTRACTION_PATTERN.matcher(str);
            }

            detailMatcher.reset(str);

            if (!detailMatcher.matches()) {
                log.error(String.format("No match found for string: %s.", str));
                return Optional.empty();
            }

            return Optional.of(HsReleaseData.builder()
                                            .subber(detailMatcher.group(1))
                                            .title(detailMatcher.group(2))
                                            .episode(Integer.parseInt(detailMatcher.group(3)))
                                            .quality(detailMatcher.group(6))
                                            .format(detailMatcher.group(10).toUpperCase(Locale.ENGLISH))
                                            .build());
        }

        public String toString() {
            return String.format(
                "[%s] %s [Ep. %s | %s | %s]", subber, title, episode, quality, format
            );
        }
    }
}
