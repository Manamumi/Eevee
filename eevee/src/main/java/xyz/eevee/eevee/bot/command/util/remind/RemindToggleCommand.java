package xyz.eevee.eevee.bot.command.util.remind;

import com.google.common.collect.ImmutableList;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.EmbedBuilder;
import xyz.eevee.eevee.action.EnforcedSafetyAction;
import xyz.eevee.eevee.bot.command.Command;
import xyz.eevee.eevee.bot.command.CommandArguments;
import xyz.eevee.eevee.bot.command.CommandGroup;
import xyz.eevee.eevee.configuration.GlobalConfiguration;
import xyz.eevee.eevee.parser.arguments.Arguments;
import xyz.eevee.eevee.parser.arguments.LiteralArgument;
import xyz.eevee.eevee.repository.model.GenericStringList;
import xyz.eevee.eevee.session.Session;
import xyz.eevee.munchlax.NewMessageEvent;

import java.util.List;
import java.util.Optional;

@Log4j2
public class RemindToggleCommand extends Command {
    public RemindToggleCommand(CommandGroup commandGroup) {
        super(commandGroup);
    }

    @Override
    public String getShortLabel() {
        return "remind.toggle";
    }

    @Override
    public String getLabel() {
        return "Toggle Reminders On/Off";
    }

    @Override
    public String getDescription() {
        return "Allows you to turn on or off whether or not other people can set reminders for you.";
    }

    @Override
    public String getExample() {
        return "ev remind toggle";
    }

    @Override
    public Arguments<? extends CommandArguments> getArguments() {
        return new Arguments<>(ImmutableList.of(
            new LiteralArgument("remind"),
            new LiteralArgument("toggle")
        ), RemindToggleCommandArguments.class);
    }

    @Override
    public void invoke(
        @NonNull NewMessageEvent event,
        @NonNull CommandArguments arguments
    ) {
        Optional<GenericStringList> genericStringListOptional = Session.getSession()
                                                                       .getStringListDataRepository()
                                                                       .getStringList(
                                                                           GlobalConfiguration.REMINDER_OPT_OUT_LIST_KEY
                                                                       );

        String userId = event.getAuthor().getId();


        EnforcedSafetyAction action = EnforcedSafetyAction.builder()
                                                          .build();

        if (!genericStringListOptional.isPresent()) {
            Session.getSession().getStringListDataRepository().add(
                GlobalConfiguration.REMINDER_OPT_OUT_LIST_KEY,
                userId
            );

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Reminder Opt-Out Status");
            embedBuilder.setDescription(
                "You have been opted out of reminders from other people."
            );
            embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.defaultEmbedColorDecimal"));
            action.sendEmbedMessage(e -> {
                log.warn("Failed to send reminder opt-out status as embed.", e);
            }, event.getChannelId(), embedBuilder.build());
            return;
        }

        GenericStringList genericStringList = genericStringListOptional.get();
        List<String> optedOutUsers = genericStringList.getList();
        boolean wasOptedOut = true;

        if (optedOutUsers.contains(userId)) {
            optedOutUsers.remove(userId);
            wasOptedOut = false;
        } else {
            optedOutUsers.add(userId);
        }

        Session.getSession().getStringListDataRepository().update(genericStringList);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Reminder Opt-Out Status");
        embedBuilder.setDescription(
            wasOptedOut ?
                "You have been opted out of reminders from other people." :
                "You have been opted in to reminders from other people."
        );
        embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.defaultEmbedColorDecimal"));
        action.sendEmbedMessage(e -> {
            log.warn("Failed to send reminder opt-out status as embed.", e);
        }, event.getChannelId(), embedBuilder.build());
    }
}
