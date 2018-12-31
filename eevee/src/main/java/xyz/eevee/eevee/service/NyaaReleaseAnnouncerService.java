package xyz.eevee.eevee.service;

import com.google.common.collect.Lists;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import xyz.eevee.eevee.action.EnforcedSafetyAction;
import xyz.eevee.eevee.provider.NyaaReleaseProvider;
import xyz.eevee.eevee.provider.model.NyaaRelease;
import xyz.eevee.eevee.repository.model.NyaaReleaseAnnouncer;
import xyz.eevee.eevee.session.Session;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Log4j2
public class NyaaReleaseAnnouncerService implements Service {
    private static NyaaReleaseAnnouncerService serviceInstance;

    public static NyaaReleaseAnnouncerService getInstance() {
        if (serviceInstance == null) {
            serviceInstance = new NyaaReleaseAnnouncerService();
        }

        return serviceInstance;
    }

    @Override
    public void start() {
        Thread thread = new Thread("NyaaReleaseAnnouncerServiceThread") {
            public void run() {
                while (true) {
                    checkOnce();

                    try {
                        TimeUnit.MILLISECONDS.sleep(
                            Session.getSession().getConfiguration().readInt("eevee.nyaaReleaseCheckDelay")
                        );
                    } catch (InterruptedException e) {
                        log.warn("Failed to sleep after checking for Nyaa releases. Will try again.", e);
                    }
                }
            }
        };

        thread.start();
    }

    private void checkOnce() {
        log.info("Checking for new Nyaa releases.");

        List<NyaaReleaseAnnouncer> announcerList = Session.getSession()
                                                              .getNyaaReleaseAnnouncerDataRepository()
                                                              .getAnnouncers();

        Optional<List<NyaaRelease>> releasesOptional = NyaaReleaseProvider.getReleases();

        if (!releasesOptional.isPresent()) {
            log.info("No Nyaa releases found. Skipping.");
            return;
        }

        List<NyaaRelease> releases = releasesOptional.get();
        releases = Lists.reverse(releases);

        for (NyaaRelease release : releases) {
            announcerList.stream()
                         .filter(a -> a.getAnime().equalsIgnoreCase(release.getTitle()))
                         .filter(a -> a.getSubber().equalsIgnoreCase(release.getSubber()))
                         .filter(a -> a.getQuality().equalsIgnoreCase(release.getQuality()))
                         .filter(a -> a.getLastEpisode() == null || release.getPubDate().isAfter(a.getLastEpisode()))
                         .forEach(a -> {
                             announce(a, release);
                         });
        }
    }

    private void announce(NyaaReleaseAnnouncer announcer, NyaaRelease release) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.defaultEmbedColorDecimal"));

        embedBuilder.setTitle(
            String.format("New Episode of *%s* Released!", release.getTitle())
        );
        embedBuilder.addField("Episode", Integer.toString(release.getEpisode()), true);
        embedBuilder.addField("Subber", release.getSubber(), true);
        embedBuilder.addField("Format", String.format(
            "%s [%s]", release.getFormat(), release.getQuality() != null ? release.getQuality() : "N/A"
        ), true);

        TextChannel channel = Session.getSession()
                                     .getJdaClient()
                                     .getTextChannelById(announcer.getChannelId());

        if (channel == null) {
            log.debug("Found announcer for channel that no longer exists. Removing announcer.");
            Session.getSession().getNyaaReleaseAnnouncerDataRepository().remove(announcer);
            return;
        }

        EnforcedSafetyAction.builder()
                            .build()
                            .sendEmbedMessage(e -> {
                                log.warn(
                                    String.format("Failed to send release announcement to channel %s.", channel.getId()),
                                    e
                                );
                            }, channel.getId(), embedBuilder.build(), m -> {
                                log.info(String.format(
                                    "Issued announcement for new release to channel: %s.", announcer.getChannelId())
                                );
                                announcer.setLastEpisode(release.getPubDate());
                                Session.getSession().getNyaaReleaseAnnouncerDataRepository().update(announcer);
                            });

    }
}
