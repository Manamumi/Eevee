package xyz.eevee.eevee.bot.command.subscription.manga.mangadex;

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
public class MangaDexReleaseListCommand extends Command {
    public MangaDexReleaseListCommand(CommandGroup commandGroup) {
        super(commandGroup);
    }

    @Override
    public String getShortLabel() {
        return "manga.mangadex.list";
    }

    @Override
    public String getLabel() {
        return "List MangaDex Release Subscriptions";
    }

    @Override
    public String getDescription() {
        return "Show all MangaDex release subscriptions for the current channel.";
    }

    @Override
    public Arguments<? extends CommandArguments> getArguments() {
        return new Arguments<>(ImmutableList.of(
            new LiteralArgument("manga"),
            new LiteralArgument("list")
        ), MangaDexReleaseListCommandArguments.class);
    }

    @Override
    public String getExample() {
        return "ev manga list";
    }

    @Override
    public void invoke(@NonNull NewMessageEvent event, @NonNull CommandArguments arguments) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("MangaDex Release Subscriptions");

        Session.getSession()
               .getMangaDexReleaseAnnouncerDataRepository()
               .getAnnouncers()
               .stream()
               .filter(announcer -> announcer.getChannelId().equals(event.getChannelId()))
               .forEach(announcer -> {
                   embedBuilder.addField(
                       String.format("%s [%s]", announcer.getTitle(), announcer.getScanlator()),
                       announcer.getAnnouncerId(),
                       false
                   );
               });

        embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.defaultEmbedColorDecimal"));

        EnforcedSafetyAction.builder()
                            .build()
                            .sendEmbedMessage(e -> {
                                log.error("Failed to send MangaDex subscription list as embed.", e);
                            }, event.getChannelId(), embedBuilder.build());
    }
}
