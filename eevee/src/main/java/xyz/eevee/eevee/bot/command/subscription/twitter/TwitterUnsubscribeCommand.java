package xyz.eevee.eevee.bot.command.subscription.twitter;

import com.google.common.collect.ImmutableList;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.EmbedBuilder;
import xyz.eevee.eevee.action.EnforcedSafetyAction;
import xyz.eevee.eevee.bot.command.Command;
import xyz.eevee.eevee.bot.command.CommandArguments;
import xyz.eevee.eevee.bot.command.CommandGroup;
import xyz.eevee.eevee.parser.arguments.Arguments;
import xyz.eevee.eevee.parser.arguments.LiteralArgument;
import xyz.eevee.eevee.parser.arguments.StringArgument;
import xyz.eevee.eevee.repository.model.TweetAnnouncer;
import xyz.eevee.eevee.session.Session;
import xyz.eevee.eevee.util.Formatter;
import xyz.eevee.munchlax.NewMessageEvent;
import xyz.eevee.munchlax.Permission;

import java.util.List;
import java.util.Optional;

@Log4j2
public class TwitterUnsubscribeCommand extends Command {
    public TwitterUnsubscribeCommand(CommandGroup commandGroup) {
        super(commandGroup);
    }

    @Override
    public String getShortLabel() {
        return "twitter.unsubscribe";
    }

    @Override
    public String getLabel() {
        return "Unsubscribe from Somebody's Tweets";
    }

    @Override
    public String getDescription() {
        return "Unsubscribes from a user's tweets. New tweets will no longer be announced in this channel. " +
            "This command requires manage channel permission.";
    }

    @Override
    public String getExample() {
        return "ev twitter unsubscribe PlayMaple2";
    }

    @Override
    public List<Permission> getRequiredPermissions() {
        return ImmutableList.of(Permission.MANAGE_CHANNEL);
    }

    @Override
    public Arguments<TwitterUnsubscribeCommandArguments> getArguments() {
        return new Arguments<>(ImmutableList.of(
            new LiteralArgument("twitter"),
            new LiteralArgument("unsubscribe"),
            new StringArgument("user")
        ), TwitterUnsubscribeCommandArguments.class);
    }

    @Override
    public void invoke(@NonNull NewMessageEvent event, @NonNull CommandArguments arguments) {
        TwitterUnsubscribeCommandArguments args = (TwitterUnsubscribeCommandArguments) arguments;
        String user = Formatter.formatTwitterUser(args.getUser());

        Optional<TweetAnnouncer> tweetAnnouncerOptional = Session.getSession()
                                                                 .getTweetAnnouncerDataRepository()
                                                                 .getAnnouncer(user, event.getChannelId());

        if (!tweetAnnouncerOptional.isPresent()) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Oops! An error occurred.");
            embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.errorEmbedColorDecimal"));
            embedBuilder.setDescription(
                String.format("A subscription for the user `%s` does not exist.", user)
            );

            EnforcedSafetyAction.builder()
                                .build()
                                .sendEmbedMessage(e -> {
                                    log.error("Failed to send Twitter subscription update as embed.", e);
                                }, event.getChannelId(), embedBuilder.build());
            return;
        }

        Session.getSession().getTweetAnnouncerDataRepository().remove(tweetAnnouncerOptional.get());

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Twitter User Subscription Removed");
        embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.successEmbedColorDecimal"));
        embedBuilder.setDescription(
            String.format(
                "Okay. This channel will no longer receive announcements when `%s` tweets something new.",
                user
            )
        );

        EnforcedSafetyAction.builder()
                            .build()
                            .sendEmbedMessage(e -> {
                                log.error("Failed to send Twitter subscription update as embed.", e);
                            }, event.getChannelId(), embedBuilder.build());
    }
}
