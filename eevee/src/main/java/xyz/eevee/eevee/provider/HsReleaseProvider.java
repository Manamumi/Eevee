package xyz.eevee.eevee.provider;

import com.google.common.collect.ImmutableList;
import lombok.extern.log4j.Log4j2;
import xyz.eevee.eevee.provider.model.HsRelease;
import xyz.eevee.eevee.rss.HorribleSubsReleaseReader;
import xyz.eevee.eevee.rss.model.HorribleSubsReleaseItem;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Log4j2
public class HsReleaseProvider {
    /**
     * Fetches a list of HS releaes from the release RSS feed.
     *
     * @return An optional list of HS releases. If an error occurs while fetching the data then an empty Optional
     * will be returned. In all other cases the Optional will be populated even if the list is empty.
     */
    public static Optional<List<HsRelease>> getReleases() {
        log.debug("Checking for new HS releases.");

        Optional<List<HorribleSubsReleaseItem>> releasesOptional = new HorribleSubsReleaseReader().readFeed();

        if (!releasesOptional.isPresent()) {
            log.debug("Found no HS release items.");
            return Optional.empty();
        }

        List<HorribleSubsReleaseItem> releases = releasesOptional.get();

        log.debug(String.format("Found %s HS release items.", releases.size()));

        List<HsRelease> releaseList = new LinkedList<>();

        releases.forEach(release -> {
            Optional<HsRelease.HsReleaseData> releaseDataOptional =
                HsRelease.HsReleaseData.fromString(release.getTitle());

            if (!releaseDataOptional.isPresent()) {
                return;
            }

            HsRelease.HsReleaseData releaseData = releaseDataOptional.get();

            releaseList.add(
                HsRelease.builder()
                         .releaseData(releaseData)
                         .magnetLink(release.getLink())
                         .build()
            );
        });

        if (releaseList.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(ImmutableList.copyOf(releaseList));
    }
}
