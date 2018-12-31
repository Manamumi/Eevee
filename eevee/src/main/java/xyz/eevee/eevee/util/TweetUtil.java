package xyz.eevee.eevee.util;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import twitter4j.Status;
import xyz.eevee.eevee.session.Session;

@Log4j2
public class TweetUtil {
    /**
     * Given a tweet, format it into an a e s t h e t i c a l l y pleasing
     * embed and return the MessageEmbed object.
     *
     * @param tweet A Twitter tweet status to generate a Discord embed for.
     * @return A MessageEmbed representation of the tweet.
     */
    public static MessageEmbed createTweetMessageEmbed(@NonNull Status tweet) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.defaultEmbedColorDecimal"));

        String tweetType = "Tweet";

        if (tweet.isRetweet()) {
            tweetType = "Retweet";
        }

        embedBuilder.setTitle(
            String.format("New %s From %s", tweetType, tweet.getUser().getName()),
            String.format(
                Session.getSession().getConfiguration().readString("eevee.twitterPermalinkTemplate"),
                tweet.getUser().getScreenName(),
                tweet.getId()
            )
        );
        embedBuilder.setDescription(tweet.getText());
        embedBuilder.setFooter(
            String.format("@%s", tweet.getUser().getScreenName()),
            tweet.getUser().getProfileImageURL()
        );

        if (tweet.getMediaEntities().length > 0) {
            String mediaEntityUrl = tweet.getMediaEntities()[0].getMediaURL();
            log.debug(String.format("Found media entity (most likely an image): %s", mediaEntityUrl));
            embedBuilder.setImage(tweet.getMediaEntities()[0].getMediaURL());
        }

        return embedBuilder.build();
    }
}
