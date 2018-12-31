package xyz.eevee.eevee.provider;

import com.google.common.collect.ImmutableList;
import lombok.extern.log4j.Log4j2;
import xyz.eevee.eevee.provider.model.NyaaRelease;
import xyz.eevee.eevee.rss.NyaaReleaseReader;
import xyz.eevee.eevee.rss.model.NyaaReleaseItem;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Log4j2
public class NyaaReleaseProvider {
    public static Optional<List<NyaaRelease>> getReleases() {
        log.debug("Checking for new Nyaa releases.");

        Optional<List<NyaaReleaseItem>> releasesOptional = new NyaaReleaseReader().readFeed();

        if (!releasesOptional.isPresent()) {
            log.debug("Found no Nyaa release items.");
            return Optional.empty();
        }

        List<NyaaReleaseItem> releases = releasesOptional.get();

        log.debug(String.format("Found %s Nyaa release items.", releases.size()));

        List<NyaaRelease> releaseList = new LinkedList<>();

        releases.forEach(release -> {
            Optional<NyaaRelease> releaseOptional = NyaaRelease.fromReleaseItem(release);

            if (!releaseOptional.isPresent()) {
                return;
            }

            releaseList.add(releaseOptional.get());
        });

        if (releaseList.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(ImmutableList.copyOf(releaseList));
    }
}
