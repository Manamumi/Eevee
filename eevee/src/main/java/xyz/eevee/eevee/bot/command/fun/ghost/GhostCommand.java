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
import xyz.eevee.eevee.parser.arguments.MemberArgument;
import xyz.eevee.eevee.repository.model.GenericStringList;
import xyz.eevee.eevee.session.Session;
import xyz.eevee.eevee.util.Formatter;
import xyz.eevee.munchlax.NewMessageEvent;
import xyz.eevee.munchlax.User;

import java.util.Optional;

@Log4j2
public class GhostCommand extends Command {
    public GhostCommand(CommandGroup commandGroup) {
        super(commandGroup);
    }

    @Override
    public String getShortLabel() {
        return "ghost";
    }

    @Override
    public String getLabel() {
        return "Ghost Somebody";
    }

    @Override
    public String getDescription() {
        return "Deletes your ping of a user to annoy them with invisible pings.";
    }

    @Override
    public String getExample() {
        return "ev ghost @Someone";
    }

    @Override
    public Arguments<? extends CommandArguments> getArguments() {
        return new Arguments<>(ImmutableList.of(
            new LiteralArgument("ghost"),
            new MemberArgument("ghostee")
        ), GhostCommandArguments.class);
    }

    @Override
    public void invoke(@NonNull NewMessageEvent event, @NonNull CommandArguments arguments) {
        GhostCommandArguments args = (GhostCommandArguments) arguments;

        if (isOptedOut(args.getGhostee().getUser())) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(
                String.format(
                    "Unable to Ghost %s",
                    Formatter.formatTag(args.getGhostee().getUser())
                )
            );
            embedBuilder.setDescription("This user has opted-out of being ghosted.");
            embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.errorEmbedColorDecimal"));

            EnforcedSafetyAction.builder()
                                .build()
                                .sendEmbedMessage(e -> {
                                    log.warn("Failed to send opted-out user message.", e);
                                }, event.getChannelId(), embedBuilder.build());
            return;
        }

        EnforcedSafetyAction.builder()
                            .build()
                            .deleteMessage(e -> {
                                log.warn("Failed to delete ghost message.", e);
                            }, event.getChannelId(), event.getId());
    }

    private boolean isOptedOut(@NonNull User user) {
        Optional<GenericStringList> genericStringListOptional = Session.getSession()
                                                                       .getStringListDataRepository()
                                                                       .getStringList(
                                                                           GlobalConfiguration.GHOST_OPT_OUT_LIST_KEY
                                                                       );

        if (!genericStringListOptional.isPresent()) {
            return false;
        }

        return genericStringListOptional.get().getList().contains(user.getId());
    }
}
