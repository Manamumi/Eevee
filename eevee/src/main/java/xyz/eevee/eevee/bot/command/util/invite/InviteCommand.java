package xyz.eevee.eevee.bot.command.util.invite;

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
public class InviteCommand extends Command {
    public InviteCommand(CommandGroup commandGroup) {
        super(commandGroup);
    }

    @Override
    public String getShortLabel() {
        return "invite";
    }

    @Override
    public String getLabel() {
        return "Eevee Invite Link";
    }

    @Override
    public String getDescription() {
        return "Provides you with a link to invite Eevee to your server.";
    }

    @Override
    public String getExample() {
        return "ev invite";
    }

    @Override
    public Arguments<InviteCommandArguments> getArguments() {
        return new Arguments<>(ImmutableList.of(
            new LiteralArgument("invite")
        ), InviteCommandArguments.class);
    }

    @Override
    public void invoke(@NonNull NewMessageEvent event, @NonNull CommandArguments arguments) {
        String link = Session.getSession().getConfiguration().readString("eevee.eeveeInviteLink");

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Eevee Invite Link", link);
        embedBuilder.setDescription("Here's your invite link.");
        embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.defaultEmbedColorDecimal"));

        EnforcedSafetyAction.builder()
                            .build()
                            .sendEmbedMessage(e -> {
                                log.warn("Failed to send Eevee invite link as embed.", e);
                            }, event.getChannelId(), embedBuilder.build());
    }
}