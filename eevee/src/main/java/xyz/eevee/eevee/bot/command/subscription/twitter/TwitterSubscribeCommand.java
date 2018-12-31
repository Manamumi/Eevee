package xyz.eevee.eevee.bot.command.subscription.twitter;

import com.google.common.collect.ImmutableList;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.EmbedBuilder;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import xyz.eevee.eevee.action.EnforcedSafetyAction;
import xyz.eevee.eevee.bot.command.Command;
import xyz.eevee.eevee.bot.command.CommandArguments;
import xyz.eevee.eevee.bot.command.CommandGroup;
import xyz.eevee.eevee.parser.arguments.Arguments;
import xyz.eevee.eevee.parser.arguments.LiteralArgument;
import xyz.eevee.eevee.parser.arguments.StringArgument;
import xyz.eevee.eevee.provider.TwitterClientProvider;
import xyz.eevee.eevee.provider.TwitterTweetProvider;
import xyz.eevee.eevee.provider.UuidProvider;
import xyz.eevee.eevee.repository.model.TweetAnnouncer;
import xyz.eevee.eevee.session.Session;
import xyz.eevee.eevee.util.Formatter;
import xyz.eevee.munchlax.NewMessageEvent;
import xyz.eevee.munchlax.Permission;

import java.util.List;
import java.util.Optional;

@Log4j2
public class TwitterSubscribeCommand extends Command {
    public TwitterSubscribeCommand(CommandGroup commandGroup) {
        super(commandGroup);
    }

    @Override
    public String getShortLabel() {
        return "twitter.subscribe";
    }

    @Override
    public String getLabel() {
        return "Subscribe to Somebody's Tweets";
    }

    @Override
    public String getDescription() {
        return "Subscribes to a user's tweets. New tweets will be announced in this channel. " +
            "This command requires manage channel permission.";
    }

    @Override
    public String getExample() {
        return "ev twitter subscribe PlayMaple2";
    }

    @Override
    public List<Permission> getRequiredPermissions() {
        return ImmutableList.of(Permission.MANAGE_CHANNEL);
    }

    @Override
    public Arguments<TwitterSubscribeCommandArguments> getArguments() {
        return new Arguments<>(ImmutableList.of(
            new LiteralArgument("twitter"),
            new LiteralArgument("subscribe"),
            new StringArgument("user")
        ), TwitterSubscribeCommandArguments.class);
    }

    @Override
    public void invoke(@NonNull NewMessageEvent event, @NonNull CommandArguments arguments) {
        TwitterSubscribeCommandArguments args = (TwitterSubscribeCommandArguments) arguments;
        String user = Formatter.formatTwitterUser(args.getUser());

        if (
            Session.getSession()
                   .getTweetAnnouncerDataRepository()
                   .getAnnouncer(user, event.getChannelId())
                   .isPresent()
        ) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Oops! An error occurred.");
            embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.errorEmbedColorDecimal"));
            embedBuilder.setDescription(
                String.format("A subscription for the user `%s` already exists.", user)
            );

            EnforcedSafetyAction.builder()
                                .build()
                                .sendEmbedMessage(e -> {
                                    log.error("Failed to send Twitter subscription update as embed.", e);
                                }, event.getChannelId(), embedBuilder.build());
            return;
        }

        Twitter twitterClient = TwitterClientProvider.getInstance();

        try {
            twitterClient.getUserTimeline(user);
        } catch (TwitterException e) {
            e.printStackTrace();
            log.error(String.format("Failed to fetch twitter timeline for user: %s.", user));

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Oops! An error occurred.");
            embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.errorEmbedColorDecimal"));
            embedBuilder.setDescription(
                String.format("Failed to fetch Twitter timeline for user: `%s`.", user)
            );

            EnforcedSafetyAction.builder()
                                .build()
                                .sendEmbedMessage(ex -> {
                                    log.error("Failed to send Twitter subscription update as embed.", ex);
                                }, event.getChannelId(), embedBuilder.build());
            return;
        }

        Optional<Status> latestTweetOptional = TwitterTweetProvider.getLatestTweet(user);
        long latestTweetId = latestTweetOptional.map(Status::getId).orElse((long) 0);

        TweetAnnouncer announcer = TweetAnnouncer.builder()
                                                 .announcerId(UuidProvider.getUuid4())
                                                 .channelId(event.getChannelId())
                                                 .lastTweetId(latestTweetId)
                                                 .user(user)
                                                 .build();

        Session.getSession().getTweetAnnouncerDataRepository().add(announcer);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Twitter User Subscription Added");
        embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.successEmbedColorDecimal"));
        embedBuilder.setDescription(
            String.format("Okay. I will announce when `%s` tweets something new.", user)
        );

        EnforcedSafetyAction.builder()
                            .build()
                            .sendEmbedMessage(e -> {
                                log.error("Failed to send Twitter subscription update as embed.", e);
                            }, event.getChannelId(), embedBuilder.build());
    }
}
