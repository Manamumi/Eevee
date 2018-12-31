package xyz.eevee.eevee.service;

import com.google.common.collect.ImmutableList;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import xyz.eevee.eevee.action.EnforcedSafetyAction;
import xyz.eevee.eevee.provider.HsReleaseProvider;
import xyz.eevee.eevee.provider.model.HsRelease;
import xyz.eevee.eevee.repository.model.HsReleaseAnnouncer;
import xyz.eevee.eevee.session.Session;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Log4j2
public class HsReleaseAnnouncerService implements Service {
    private static volatile HsReleaseAnnouncerService serviceInstance;

    public static HsReleaseAnnouncerService getInstance() {
        if (serviceInstance == null) {
            serviceInstance = new HsReleaseAnnouncerService();
        }

        return serviceInstance;
    }

    @Override
    public void start() {
        Thread thread = new Thread("HorribleSubsReleaseNotificationThread") {
            public void run() {
                while (true) {
                    checkOnce();

                    try {
                        TimeUnit.MILLISECONDS.sleep(
                            Session.getSession().getConfiguration().readInt("eevee.animeReleaseCheckDelay")
                        );
                    } catch (InterruptedException e) {
                        log.warn("Failed to sleep after checking for HS releases. Will try again.", e);
                    }
                }
            }
        };

        thread.start();
    }

    private void checkOnce() {
        log.info("Checking for new HS release data.");

        List<HsReleaseAnnouncer> toNotifyList = Session.getSession()
                                                       .getHsReleaseAnnouncerDataRepository()
                                                       .getAnnouncers();
        Optional<List<HsRelease>> releaseOptional = HsReleaseProvider.getReleases();

        if (!releaseOptional.isPresent()) {
            log.info("No HS release data present. Skipping.");
            return;
        }

        log.info(
            String.format("Found %s new HS releases.", releaseOptional.get().size())
        );

        EnforcedSafetyAction action = EnforcedSafetyAction.builder()
                                                          .build();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        EmbedBuilder embedBuilder2 = new EmbedBuilder();
        embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.defaultEmbedColorDecimal"));
        embedBuilder2.setColor(Session.getSession().getConfiguration().readInt("eevee.defaultEmbedColorDecimal"));

        for (HsRelease release : releaseOptional.get()) {
            HsRelease.HsReleaseData releaseData = release.getReleaseData();

            List<HsReleaseAnnouncer> test = toNotifyList.stream()
                                                        .filter(
                                                            a -> a.getAnime().equalsIgnoreCase(releaseData.getTitle())
                                                        )
                                                        .filter(a -> a.getLastEpisode() < releaseData.getEpisode())
                                                        .filter(
                                                            a -> a.getQuality()
                                                                  .equalsIgnoreCase(
                                                                      releaseData.getQuality()
                                                                  )
                                                        )
                                                        .collect(ImmutableList.toImmutableList());

            log.debug(String.format("Found %s matching announcers for release: %s", test.size(), releaseData));

            test.forEach(a -> {
                embedBuilder.setTitle(
                    String.format("New Episode of *%s* Released!", releaseData.getTitle())
                );
                embedBuilder.addField("Episode", Integer.toString(releaseData.getEpisode()), true);
                embedBuilder.addField("Quality", releaseData.getQuality(), true);
                embedBuilder.addField("Format", releaseData.getFormat(), true);

                TextChannel channel = Session.getSession()
                                             .getJdaClient()
                                             .getTextChannelById(a.getChannelId());

                if (channel == null) {
                    log.debug("Found announcer for channel that no longer exists. Removing announcer.");
                    Session.getSession().getHsReleaseAnnouncerDataRepository().remove(a);
                    return;
                }

                action.sendEmbedMessage(e -> {
                    log.warn(
                        String.format("Failed to send release announcement to channel %s.", channel.getId()),
                        e
                    );
                }, a.getChannelId(), embedBuilder.build(), m -> {
                    log.info(String.format(
                        "Issued announcement for new release to channel: %s.", a.getChannelId())
                    );
                    a.setLastEpisode(releaseData.getEpisode());
                    Session.getSession().getHsReleaseAnnouncerDataRepository().update(a);
                });

                if (!a.isDownload()) {
                    return;
                }

                log.info("Found download flag in announcer. Enqueueing BT download job.");

                String downloadPath = String.format(
                    "%s/%s/",
                    Session.getSession().getConfiguration().readString("eevee.btDownloadRoot"),
                    releaseData.getTitle()
                );

                BtDownloadService.BtDownload btDownload =
                    BtDownloadService.BtDownload.builder()
                                                .magnetUrl(
                                                    release.getMagnetLink()
                                                )
                                                .downloadLocation(
                                                    downloadPath
                                                )
                                                .onComplete(downloadJob -> {
                                                    embedBuilder2.setTitle(
                                                        String.format(
                                                            "New Episode of *%s* Downloaded!",
                                                            releaseData.getTitle()
                                                        )
                                                    );
                                                    embedBuilder2.setDescription(
                                                        String.format(
                                                            "New episode downloaded and saved to: %s.",
                                                            downloadPath
                                                        )
                                                    );

                                                    action.sendEmbedMessage(e -> {
                                                        log.warn("Failed to send downloaded episode message.", e);
                                                    }, a.getChannelId(), embedBuilder2.build());
                                                })
                                                .build();

                BtDownloadService.getInstance().enqueue(btDownload);
            });
        }
    }
}
