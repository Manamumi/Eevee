package xyz.eevee.eevee.service;

import com.google.common.collect.Lists;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import xyz.eevee.eevee.action.EnforcedSafetyAction;
import xyz.eevee.eevee.provider.MangaDexReleaseProvider;
import xyz.eevee.eevee.provider.model.MangaDexRelease;
import xyz.eevee.eevee.repository.model.MangaDexReleaseAnnouncer;
import xyz.eevee.eevee.session.Session;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Log4j2
public class MangaDexReleaseAnnouncerService implements Service {
    private static MangaDexReleaseAnnouncerService serviceInstance;

    public static MangaDexReleaseAnnouncerService getInstance() {
        if (serviceInstance == null) {
            serviceInstance = new MangaDexReleaseAnnouncerService();
        }

        return serviceInstance;
    }

    @Override
    public void start() {
        Thread thread = new Thread("MangaDexReleaseAnnouncerServiceThread") {
            public void run() {
                while (true) {
                    checkOnce();

                    try {
                        TimeUnit.MILLISECONDS.sleep(
                            Session.getSession().getConfiguration().readInt("mangaReleaseCheckDelay")
                        );
                    } catch (InterruptedException e) {
                        log.warn("Failed to sleep after checking for MangaDex releases. Will try again.", e);
                    }
                }
            }
        };

        thread.start();
    }

    private void checkOnce() {
        log.info("Checking for new MangaDex releases.");

        List<MangaDexReleaseAnnouncer> announcerList = Session.getSession()
                                                              .getMangaDexReleaseAnnouncerDataRepository()
                                                              .getAnnouncers();

        Optional<List<MangaDexRelease>> releasesOptional = MangaDexReleaseProvider.getReleases();

        if (!releasesOptional.isPresent()) {
            log.info("No MangaDex releases found. Skipping.");
            return;
        }

        List<MangaDexRelease> releases = releasesOptional.get();
        releases = Lists.reverse(releases);

        for (MangaDexRelease release : releases) {
            announcerList.stream()
                         .filter(a -> a.getTitle().equalsIgnoreCase(release.getTitle()))
                         .filter(a -> a.getLastChapter() == null || release.getPubDate().isAfter(a.getLastChapter()))
                         .forEach(a -> {
                             announce(a, release);
                         });
        }
    }

    private void announce(MangaDexReleaseAnnouncer announcer, MangaDexRelease release) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.defaultEmbedColorDecimal"));

        embedBuilder.setTitle(
            String.format("New Chapter of *%s* Released!", release.getTitle())
        );
        embedBuilder.addField("Chapter", release.getChapter(), true);
        embedBuilder.addField("Scanlator", release.getScanlator(), true);
        embedBuilder.addField("Language", release.getLanguage(), true);

        TextChannel channel = Session.getSession()
                                     .getJdaClient()
                                     .getTextChannelById(announcer.getChannelId());

        if (channel == null) {
            log.debug("Found announcer for channel that no longer exists. Removing announcer.");
            Session.getSession().getMangaDexReleaseAnnouncerDataRepository().remove(announcer);
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
                                announcer.setLastChapter(release.getPubDate());
                                Session.getSession().getMangaDexReleaseAnnouncerDataRepository().update(announcer);
                            });

    }
}
