package xyz.eevee.eevee.bot.command.fun.ghost;

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
public class GhostToggleCommand extends Command {
    public GhostToggleCommand(CommandGroup commandGroup) {
        super(commandGroup);
    }

    @Override
    public String getShortLabel() {
        return "ghost.toggle";
    }

    @Override
    public String getLabel() {
        return "Toggle Ghosting On/Off";
    }

    @Override
    public String getDescription() {
        return "Toggles on or off whether or not other people can ghost you.";
    }

    @Override
    public Arguments<? extends CommandArguments> getArguments() {
        return new Arguments<>(ImmutableList.of(
            new LiteralArgument("ghost"),
            new LiteralArgument("toggle")
        ), GhostToggleCommandArguments.class);
    }

    @Override
    public String getExample() {
        return "ev ghost toggle";
    }

    @Override
    public void invoke(@NonNull NewMessageEvent event, @NonNull CommandArguments arguments) {
        Optional<GenericStringList> genericStringListOptional = Session.getSession()
                                                                       .getStringListDataRepository()
                                                                       .getStringList(
                                                                           GlobalConfiguration.GHOST_OPT_OUT_LIST_KEY
                                                                       );

        final String userId = event.getAuthor().getId();
        final EmbedBuilder embedBuilder = new EmbedBuilder();
        EnforcedSafetyAction action = EnforcedSafetyAction.builder()
                                                          .build();

        embedBuilder.setTitle("Ghost Opt-Out Status");
        embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.defaultEmbedColorDecimal"));

        if (!genericStringListOptional.isPresent()) {
            Session.getSession()
                   .getStringListDataRepository()
                   .add(GlobalConfiguration.GHOST_OPT_OUT_LIST_KEY, event.getAuthor().getId());

            embedBuilder.setDescription("You have been opted-out of being ghosted by other members.");
            action.sendEmbedMessage(e -> {
                log.warn("Failed to send ghost toggle message as embed.", e);
            }, event.getChannelId(), embedBuilder.build());
            return;
        }

        GenericStringList genericStringList = genericStringListOptional.get();
        List<String> stringList = genericStringList.getList();

        if (stringList.contains(userId)) {
            stringList.remove(userId);
            embedBuilder.setDescription("You have been opted-out of being ghosted by other members.");
        } else {
            stringList.add(userId);
            embedBuilder.setDescription("You have been opted-in to being ghosted by other members.");
        }

        Session.getSession().getStringListDataRepository().update(genericStringList);
        action.sendEmbedMessage(e -> {
            log.warn("Failed to send ghost toggle message as embed.", e);
        }, event.getChannelId(), embedBuilder.build());
    }
}
