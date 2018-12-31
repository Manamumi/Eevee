package xyz.eevee.eevee.bot.command.dev.tl;

import com.google.common.collect.ImmutableList;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.EmbedBuilder;
import xyz.eevee.eevee.action.EnforcedSafetyAction;
import xyz.eevee.eevee.bot.CommandMapper;
import xyz.eevee.eevee.bot.command.Command;
import xyz.eevee.eevee.bot.command.CommandArguments;
import xyz.eevee.eevee.bot.command.CommandGroup;
import xyz.eevee.eevee.parser.arguments.Arguments;
import xyz.eevee.eevee.parser.arguments.LiteralArgument;
import xyz.eevee.eevee.parser.arguments.MemberArgument;
import xyz.eevee.eevee.parser.arguments.StringArgument;
import xyz.eevee.eevee.session.Session;
import xyz.eevee.eevee.target.BotOwnerTargetLock;
import xyz.eevee.eevee.target.DeveloperOnlyTargetLock;
import xyz.eevee.eevee.target.TargetLock;
import xyz.eevee.munchlax.NewMessageEvent;

import java.util.Optional;

@Log4j2
public class TargetLockCheckCommand extends Command {
    public TargetLockCheckCommand(CommandGroup commandGroup) {
        super(commandGroup);
    }

    @Override
    public String getShortLabel() {
        return "tl.check";
    }

    @Override
    public String getLabel() {
        return "Check Command Target Lock";
    }

    @Override
    public String getDescription() {
        return "Shows whether or not a user can access a command by checking its target lock.";
    }

    @Override
    public Arguments<? extends CommandArguments> getArguments() {
        return new Arguments<>(ImmutableList.of(
            new LiteralArgument("tlc"),
            new StringArgument("shortLabel"),
            new MemberArgument("user")
        ), TargetLockCheckCommandArguments.class);
    }

    @Override
    public String getExample() {
        return "ev tcp f12 @Someone";
    }

    @Override
    public void invoke(
        @NonNull NewMessageEvent event,
        @NonNull CommandArguments arguments
    ) {
        TargetLockCheckCommandArguments args = (TargetLockCheckCommandArguments) arguments;
        final CommandMapper commandMapper = Session.getSession()
                                                   .getCommandMapper();

        Optional<Command> commandOptional = commandMapper.getBotCommands()
                                                         .stream()
                                                         .filter(c -> c.getShortLabel().equals(args.getShortLabel()))
                                                         .findFirst();

        EnforcedSafetyAction action = EnforcedSafetyAction.builder()
                                                          .build();

        EmbedBuilder embedBuilder = new EmbedBuilder();

        if (!commandOptional.isPresent()) {
            embedBuilder.setTitle("Oops! An error occurred.");
            embedBuilder.setDescription("Invalid command specified.");
            embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.errorEmbedColorDecimal"));
        } else {
            final boolean isBotOwner = new BotOwnerTargetLock().check(args.getUser());
            final boolean isDeveloper = new DeveloperOnlyTargetLock().check(args.getUser());
            final TargetLock targetLock = commandOptional.get().getTargetLock();
            final boolean passesLock = targetLock.check(args.getUser());
            final TargetLock groupLock = commandOptional.get().getCommandGroup().getTargetLock();
            final boolean passesGroupLock = groupLock.check(args.getUser());

            String nameDiscrim = String.format(
                "%s#%s",
                args.getUser().getUser().getUsername(),
                args.getUser().getUser().getDiscriminator()
            );

            embedBuilder.setTitle(
                String.format(
                    "Can %s# (%s) access \"%s\"?",
                    nameDiscrim,
                    args.getUser().getUser().getId(),
                    args.getShortLabel()
                )
            );

            if (isBotOwner || (passesGroupLock && passesLock)) {
                embedBuilder.setDescription(
                    String.format("%s is able to access this command.", nameDiscrim)
                );
                embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.successEmbedColorDecimal"));

                if (isBotOwner) {
                    embedBuilder.appendDescription("\nThis user is the bot owner and bypasses all target locks.");
                }
            } else {
                embedBuilder.setDescription(
                    String.format("%s is not able to access this command.", nameDiscrim)
                );
                embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.errorEmbedColorDecimal"));
            }

            embedBuilder.addField(
                "Group Lock", groupLock.getClass().getSimpleName(), false
            );

            embedBuilder.addField(
                "Target Lock", targetLock.getClass().getSimpleName(), false
            );

            embedBuilder.addField(
                "Is Bot Owner?",
                String.format(
                    isBotOwner ?
                        "%s is the bot owner." :
                        "%s is not the bot owner.",
                    nameDiscrim
                ),
                false
            );
            embedBuilder.addField(
                "Is Eevee Developer?",
                String.format(
                    isDeveloper ?
                        "%s is an Eevee developer." :
                        "%s is not an Eevee developer.",
                    nameDiscrim
                ),
                false
            );
            embedBuilder.addField(
                "Passes Target Lock?",
                String.format(
                    passesGroupLock && passesLock ?
                        "%s passes the target lock." :
                        "%s does not pass the target lock.",
                    nameDiscrim
                ),
                false
            );
        }

        action.sendEmbedMessage(e -> {
            log.warn("Failed to send target lock information as embed.", e);
        }, event.getChannelId(), embedBuilder.build());
    }
}
