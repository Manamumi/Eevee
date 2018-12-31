package xyz.eevee.eevee.bot.command.dev.coffee;

import com.google.common.collect.ImmutableList;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.EmbedBuilder;
import xyz.eevee.eevee.action.EnforcedSafetyAction;
import xyz.eevee.eevee.bot.command.Command;
import xyz.eevee.eevee.bot.command.CommandArguments;
import xyz.eevee.eevee.bot.command.CommandGroup;
import xyz.eevee.eevee.configuration.CoffeeConfiguration;
import xyz.eevee.eevee.exc.EeveeActionException;
import xyz.eevee.eevee.parser.arguments.Arguments;
import xyz.eevee.eevee.parser.arguments.LiteralArgument;
import xyz.eevee.eevee.provider.MongoClientProvider;
import xyz.eevee.eevee.provider.TwitterClientProvider;
import xyz.eevee.eevee.repository.HsReleaseAnnouncerDataRepository;
import xyz.eevee.eevee.repository.ReminderDataRepository;
import xyz.eevee.eevee.repository.StringListDataRepository;
import xyz.eevee.eevee.repository.TweetAnnouncerDataRepository;
import xyz.eevee.eevee.session.Session;
import xyz.eevee.munchlax.NewMessageEvent;

import java.util.List;
import java.util.function.Consumer;

@Log4j2
public class BrewCommand extends Command {
    public BrewCommand(CommandGroup commandGroup) {
        super(commandGroup);
    }

    @Override
    public String getShortLabel() {
        return "coffee.brew";
    }

    @Override
    public String getLabel() {
        return "Soft Reload Eevee";
    }

    @Override
    public String getDescription() {
        return "Invalidates the current configuration cache and soft reloads Eevee.";
    }

    @Override
    public String getExample() {
        return "ev brew coffee";
    }

    @Override
    public Arguments<? extends CommandArguments> getArguments() {
        return new Arguments<>(ImmutableList.of(
            new LiteralArgument("brew"),
            new LiteralArgument("coffee")
        ), BrewCommandArguments.class);
    }

    @Override
    public synchronized void invoke(@NonNull NewMessageEvent event, @NonNull CommandArguments arguments) {
        final EnforcedSafetyAction action = EnforcedSafetyAction.builder()
                                                                .build();

        Consumer<EeveeActionException> failureHandler = (e -> {
            log.warn("Failed to send coffee reload status in message.", e);
        });

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.defaultEmbedColorDecimal"));

        embedBuilder.setTitle("Throwing Away Old Coffee...");
        embedBuilder.setDescription("Invalidating all currently cached configuration values...");
        action.sendEmbedMessage(failureHandler, event.getChannelId(), embedBuilder.build());

        List<String> invalidatedKeys = ((CoffeeConfiguration) Session.getSession()
                                                                     .getConfiguration()).invalidateCache();

        embedBuilder.setTitle("Cleaning French Press...");
        embedBuilder.setDescription(
            String.format(
                "The following configuration values have been invalidated and will be re-fetched on-demand:```%n%s```" +
                    "Configuration values used for startup such as `botToken` will require a `ev restart` " +
                    "in order to take effect.",
                String.join("\n", invalidatedKeys)
            )
        );
        action.sendEmbedMessage(failureHandler, event.getChannelId(), embedBuilder.build());

        embedBuilder.setTitle("Steeping Fresh Coffee Grounds...");
        embedBuilder.setDescription("Data providers are being reloaded. Please wait.");
        action.sendEmbedMessage(e -> {
            log.warn("Failed to send coffee reload status in message.", e);
        }, event.getChannelId(), embedBuilder.build());

        MongoClientProvider.invalidate();
        TwitterClientProvider.invalidate();

        embedBuilder.setTitle("Pressing Coffee...");
        embedBuilder.setDescription("Data repositories are being reloaded. Please wait.");
        action.sendEmbedMessage(failureHandler, event.getChannelId(), embedBuilder.build());

        Session.getSession().setReminderDataRepository(ReminderDataRepository.reload());
        Session.getSession().setHsReleaseAnnouncerDataRepository(HsReleaseAnnouncerDataRepository.reload());
        Session.getSession().setTweetAnnouncerDataRepository(TweetAnnouncerDataRepository.reload());
        Session.getSession().setStringListDataRepository(StringListDataRepository.reload());

        embedBuilder.setTitle("Ready to Pour!");
        embedBuilder.setDescription("Eevee has been soft reloaded.");
        embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.successEmbedColorDecimal"));
        action.sendEmbedMessage(failureHandler, event.getChannelId(), embedBuilder.build());
    }
}
