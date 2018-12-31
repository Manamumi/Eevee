package xyz.eevee.eevee.bot.command.subscription.anime.nyaa;

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
import xyz.eevee.eevee.session.Session;
import xyz.eevee.munchlax.NewMessageEvent;

@Log4j2
public class NyaaReleaseListCommand extends Command {
    public NyaaReleaseListCommand(CommandGroup commandGroup) {
        super(commandGroup);
    }

    @Override
    public String getShortLabel() {
        return "anime.nyaa.list";
    }

    @Override
    public String getLabel() {
        return "List Nyaa Release Subscriptions";
    }

    @Override
    public String getDescription() {
        return "Show all Nyaa release subscriptions for the current channel.";
    }

    @Override
    public Arguments<? extends CommandArguments> getArguments() {
        return new Arguments<>(ImmutableList.of(
            new LiteralArgument("nyaa"),
            new LiteralArgument("list")
        ), NyaaReleaseListCommandArguments.class);
    }

    @Override
    public String getExample() {
        return "ev nyaa list";
    }

    @Override
    public void invoke(@NonNull NewMessageEvent event, @NonNull CommandArguments arguments) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Nyaa Release Subscriptions");

        Session.getSession()
               .getNyaaReleaseAnnouncerDataRepository()
               .getAnnouncers()
               .stream()
               .filter(announcer -> announcer.getChannelId().equals(event.getChannelId()))
               .forEach(announcer -> {
                   embedBuilder.addField(
                       String.format(
                           "[%s] %s - %s",
                           announcer.getSubber(),
                           announcer.getAnime(),
                           announcer.getQuality()
                       ),
                       announcer.getAnnouncerId(),
                       false
                   );
               });

        embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.defaultEmbedColorDecimal"));

        EnforcedSafetyAction.builder()
                            .build()
                            .sendEmbedMessage(e -> {
                                log.error("Failed to send Nyaa subscription list as embed.", e);
                            }, event.getChannelId(), embedBuilder.build());
    }
}
