package xyz.eevee.eevee.bot.command.subscription.anime.horriblesubs;

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
public class HsReleaseListCommand extends Command {
    public HsReleaseListCommand(CommandGroup commandGroup) {
        super(commandGroup);
    }

    @Override
    public String getShortLabel() {
        return "anime.hs.list";
    }

    @Override
    public String getLabel() {
        return "List HorribleSubs Release Subscriptions";
    }

    @Override
    public String getDescription() {
        return "Show all HorribleSubs release subscriptions for the current channel.";
    }

    @Override
    public Arguments<HsReleaseListCommandArguments> getArguments() {
        return new Arguments<>(ImmutableList.of(
            new LiteralArgument("hs"),
            new LiteralArgument("list")
        ), HsReleaseListCommandArguments.class);
    }

    @Override
    public String getExample() {
        return "ev hs list";
    }

    @Override
    public void invoke(@NonNull NewMessageEvent event, @NonNull CommandArguments arguments) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("HorribleSubs Release Subscriptions");

        Session.getSession()
               .getHsReleaseAnnouncerDataRepository()
               .getAnnouncers()
               .stream()
               .filter(announcer -> announcer.getChannelId().equals(event.getChannelId()))
               .forEach(announcer -> {
                   embedBuilder.addField(
                       String.format("%s [%s]", announcer.getAnime(), announcer.getQuality()),
                       announcer.getAnnouncerId(),
                       false
                   );
               });

        embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.defaultEmbedColorDecimal"));

        EnforcedSafetyAction.builder()
                            .build()
                            .sendEmbedMessage(e -> {
                                log.error("Failed to send HS subscription list as embed.", e);
                            }, event.getChannelId(), embedBuilder.build());
    }
}
