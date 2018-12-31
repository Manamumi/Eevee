package xyz.eevee.eevee.service;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.entities.TextChannel;
import twitter4j.Status;
import xyz.eevee.eevee.action.EnforcedSafetyAction;
import xyz.eevee.eevee.provider.TwitterTweetProvider;
import xyz.eevee.eevee.repository.TweetAnnouncerDataRepository;
import xyz.eevee.eevee.repository.model.TweetAnnouncer;
import xyz.eevee.eevee.session.Session;
import xyz.eevee.eevee.util.TweetUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Log4j2
public class TemporalTweetAnnouncerService implements Service {
    private static volatile TemporalTweetAnnouncerService serviceInstance;

    public static TemporalTweetAnnouncerService getInstance() {
        if (serviceInstance == null) {
            serviceInstance = new TemporalTweetAnnouncerService();
        }

        return serviceInstance;
    }

    @Override
    public void start() {
        Thread thread = new Thread("TemporalTweetNotificationThread") {
            public void run() {
                while (true) {
                    checkOnce();

                    try {
                        TimeUnit.MILLISECONDS.sleep(
                            Session.getSession().getConfiguration().readInt("eevee.tweetCheckDelay")
                        );
                    } catch (InterruptedException e) {
                        log.warn("Failed to sleep after checking for new tweets. Will try again.", e);
                    }
                }
            }
        };

        thread.start();
    }

    private void checkOnce() {
        log.info("Checking for new tweets.");

        TweetAnnouncerDataRepository dataRepository = Session.getSession().getTweetAnnouncerDataRepository();
        List<TweetAnnouncer> announcerList = dataRepository.getAnnouncers();

        // Cache tweets to avoid having to fetch multiple times if a user
        // is subscribed to in multiple channels.
        Map<String, List<Status>> latestTweetMap = new HashMap<>();

        EnforcedSafetyAction action = EnforcedSafetyAction.builder()
                                                          .build();

        announcerList.forEach(announcer -> {
            if (announcer.getLastTweetTimestamp() == null) {
                log.debug(
                    "Found announcer with no last tweet timestamp. " +
                        "Updating timestamp to latest tweet and skipping."
                );

                Optional<Status> latestTweet = TwitterTweetProvider.getLatestTweet(announcer.getUser());

                if (latestTweet.isPresent()) {
                    announcer.setLastTweetTimestamp(latestTweet.get().getCreatedAt().toInstant());
                    Session.getSession().getTweetAnnouncerDataRepository().update(announcer);
                }

                return;
            }

            log.info(String.format("Checking for new tweet from %s.", announcer.getUser()));
            List<Status> latestTweets;

            final int TWEET_FETCH_COUNT = Session.getSession().getConfiguration().readInt(
                "eevee.tweetFetchCount"
            );

            String userName = announcer.getUser().toLowerCase(Locale.ENGLISH);

            if (!latestTweetMap.containsKey(userName)) {
                Optional<List<Status>> latestTweetsOptional = TwitterTweetProvider.getLatestTweets(
                    announcer.getUser(),
                    TWEET_FETCH_COUNT
                );

                if (!latestTweetsOptional.isPresent()) {
                    log.debug(String.format("No new tweets from %s found.", announcer.getUser()));
                    return;
                }

                latestTweetMap.put(userName, latestTweetsOptional.get());
                latestTweets = latestTweetsOptional.get();
            } else {
                latestTweets = latestTweetMap.get(userName);
            }

            log.debug(String.format("Found tweets from %s.", announcer.getUser()));

            log.debug(String.format(
                "Latest tweet timestamp: %s Last seen tweet timestamp: %s.",
                latestTweets.get(0).getCreatedAt().toInstant(),
                announcer.getLastTweetTimestamp())
            );

            // Need to handle multiple new tweets between check intervals.

            boolean foundLastSeen = false;
            int splitIndex = 0;
            int page = 1;

            while (!foundLastSeen) {
                for (; splitIndex < latestTweets.size(); splitIndex++) {
                    if (
                        !latestTweets.get(splitIndex)
                                     .getCreatedAt()
                                     .toInstant()
                                     .isAfter(announcer.getLastTweetTimestamp())
                    ) {
                        foundLastSeen = true;
                        break;
                    }
                }

                if (!foundLastSeen) {
                    page++;

                    Optional<List<Status>> nextPageOptional = TwitterTweetProvider.getLatestTweets(
                        announcer.getUser(),
                        page,
                        TWEET_FETCH_COUNT
                    );

                    if (!nextPageOptional.isPresent()) {
                        log.debug("Could not find more tweets to determine split index. Assuming all tweets unseen.");
                    } else {
                        latestTweets.addAll(nextPageOptional.get());
                    }
                }
            }

            List<Status> newTweets = latestTweets.subList(0, splitIndex);

            // Work backwards because reversing the collection is expensive.
            for (int n = newTweets.size() - 1; n >= 0; n--) {
                Status tweet = newTweets.get(n);

                TextChannel channel = Session.getSession()
                                             .getJdaClient()
                                             .getTextChannelById(announcer.getChannelId());

                if (channel != null) {
                    action.sendEmbedMessage(error -> {
                        log.error(String.format(
                            "Failed to send tweet announcement to channel %s.",
                            channel.getId()),
                            error
                        );
                    }, channel.getId(), TweetUtil.createTweetMessageEmbed(tweet), m -> {
                        announcer.setLastTweetTimestamp(tweet.getCreatedAt().toInstant());
                        Session.getSession().getTweetAnnouncerDataRepository().update(announcer);
                    });

                    log.debug(
                        String.format(
                            "Issued announcement for new tweet from %s to channel: %s.",
                            announcer.getUser(),
                            announcer.getChannelId()
                        )
                    );
                } else {
                    Session.getSession().getTweetAnnouncerDataRepository().remove(announcer);
                    log.debug("Found announcer for channel that no longer exists. Removing announcer.");
                }
            }
        });
    }
}
