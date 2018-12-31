package xyz.eevee.eevee.provider;

import com.google.common.collect.ImmutableList;
import lombok.extern.log4j.Log4j2;
import xyz.eevee.eevee.provider.model.MangaDexRelease;
import xyz.eevee.eevee.rss.MangaDexReleaseReader;
import xyz.eevee.eevee.rss.model.MangaDexReleaseItem;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Log4j2
public class MangaDexReleaseProvider {
    public static Optional<List<MangaDexRelease>> getReleases() {
        log.debug("Checking for new MangaDex releases.");
        Optional<List<MangaDexReleaseItem>> releasesOptional = new MangaDexReleaseReader().readFeed();

        if (!releasesOptional.isPresent()) {
            log.debug("Found no MangaDex release items.");
            return Optional.empty();
        }

        List<MangaDexReleaseItem> releases = releasesOptional.get();

        log.debug(String.format("Found %s MangaDex release items.", releases.size()));

        List<MangaDexRelease> releaseList = new LinkedList<>();

        releases.forEach(release -> {
            Optional<MangaDexRelease> releaseDataOptional = MangaDexRelease.fromReleaseItem(release);

            if (!releaseDataOptional.isPresent()) {
                return;
            }

            releaseList.add(releaseDataOptional.get());
        });

        if (releaseList.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(ImmutableList.copyOf(releaseList));
    }
}
