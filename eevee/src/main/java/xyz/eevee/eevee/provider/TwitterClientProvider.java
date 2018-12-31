package xyz.eevee.eevee.provider;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import xyz.eevee.eevee.session.Session;

public class TwitterClientProvider {
    private static Twitter twitterClient;

    /**
     * Returns a Twitter API client. Subsequent calls to this method will yield the same client object.
     *
     * @return A Twitter API client.
     */
    public static Twitter getInstance() {
        if (twitterClient == null) {
            ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
            configurationBuilder.setDebugEnabled(false)
                .setOAuthConsumerKey(
                    Session.getSession().getConfiguration().readString("eevee.twitterApiConsumerKey")
                )
                .setOAuthConsumerSecret(
                    Session.getSession().getConfiguration().readString("eevee.twitterApiConsumerSecret")
                )
                .setOAuthAccessToken(
                    Session.getSession().getConfiguration().readString("eevee.twitterApiAccessToken")
                )
                .setOAuthAccessTokenSecret(
                    Session.getSession().getConfiguration().readString("eevee.twitterApiAccessSecret")
                );

            TwitterFactory twitterFactory = new TwitterFactory(configurationBuilder.build());
            twitterClient = twitterFactory.getInstance();
        }

        return twitterClient;
    }

    public static void invalidate() {
        twitterClient = null;
    }
}
