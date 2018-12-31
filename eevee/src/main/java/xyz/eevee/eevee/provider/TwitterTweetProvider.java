package xyz.eevee.eevee.provider;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.List;
import java.util.Optional;

@Log4j2
public class TwitterTweetProvider {
    /**
     * Returns the latest tweet for a given user.
     *
     * @param user The Twitter handle for a user as a string.
     * @return An optional Twitter tweet status containing the latest tweet for a user.
     */
    public static Optional<Status> getLatestTweet(@NonNull String user) {
        Optional<List<Status>> tweet = getLatestTweets(user, 1);
        return tweet.map(statuses -> statuses.get(0));
    }

    /**
     * Fetches a number of tweets for a given user up to a given maximum. This will fetch tweets starting from
     * newest to oldest.
     *
     * @param user  The Twitter handle for a user as a string.
     * @param count The maximum number of tweets to fetch.
     * @return An optional list of tweet status containing up to *count* number of tweets. If an error occurred
     * while fetching tweets then an empty optional will be returned.
     */
    public static Optional<List<Status>> getLatestTweets(@NonNull String user, int count) {
        return getLatestTweets(user, 1, count);
    }

    /**
     * Fetches a number of tweets for a given user up to a given maximum. This will fetch tweets starting from
     * newest to oldest. This method allows for pagination so you may jump to a certain starting point to fetch from.
     *
     * @param user  The Twitter handle for a user as a string.
     * @param page  The page of tweets to fetch from. This does not correspond to real "pages" but instead relates to
     *              a "paginated view" for a stream of tweets.
     * @param count The maximum number of tweets to fetch.
     * @return An optional list of tweet status containing up to *count* number of tweets. If an error occurred
     * while fetching tweets then an empty optional will be returned.
     */
    public static Optional<List<Status>> getLatestTweets(@NonNull String user, int page, int count) {
        Twitter twitter = TwitterClientProvider.getInstance();

        try {
            List<Status> statuses = twitter.getUserTimeline(user, new Paging(page, count));

            if (statuses.size() == 0) {
                return Optional.empty();
            }

            return Optional.of(statuses);
        } catch (TwitterException e) {
            log.error("Failed to fetch tweets.", e);
        }

        return Optional.empty();
    }
}
