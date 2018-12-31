package xyz.eevee.eevee.bot.command.util.help;

import com.google.common.collect.ImmutableList;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.EmbedBuilder;
import xyz.eevee.eevee.action.EnforcedSafetyAction;
import xyz.eevee.eevee.bot.CommandMapper;
import xyz.eevee.eevee.bot.command.Command;
import xyz.eevee.eevee.bot.command.CommandArguments;
import xyz.eevee.eevee.bot.command.CommandGroup;
import xyz.eevee.eevee.exc.EeveeActionException;
import xyz.eevee.eevee.parser.arguments.ArgumentOptions;
import xyz.eevee.eevee.parser.arguments.Arguments;
import xyz.eevee.eevee.parser.arguments.LiteralArgument;
import xyz.eevee.eevee.parser.arguments.StringArgument;
import xyz.eevee.eevee.session.Session;
import xyz.eevee.eevee.util.Formatter;
import xyz.eevee.munchlax.NewMessageEvent;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Log4j2
public class HelpCommand extends Command {
    public HelpCommand(CommandGroup commandGroup) {
        super(commandGroup);
    }

    @Override
    public String getShortLabel() {
        return "help";
    }

    @Override
    public String getLabel() {
        return "Show Help Information";
    }

    @Override
    public String getDescription() {
        return "Shows help information for all commands.";
    }

    @Override
    public String getExample() {
        return "ev help translate";
    }

    @Override
    public Arguments<HelpCommandArguments> getArguments() {
        return new Arguments<>(ImmutableList.of(
            new LiteralArgument("help"),
            new StringArgument("commandShortName").withOptions(
                ArgumentOptions.builder()
                               .required(false)
                               .build()
            )
        ), HelpCommandArguments.class);
    }

    @Override
    public void invoke(@NonNull NewMessageEvent event, @NonNull CommandArguments arguments) {
        HelpCommandArguments args = (HelpCommandArguments) arguments;
        EmbedBuilder embedBuilder = new EmbedBuilder();
        final String prefix = Session.getSession()
                                     .getConfiguration()
                                     .readString("eevee.botPrefix");
        final CommandMapper commandMapper = Session.getSession()
                                                   .getCommandMapper();

        final EnforcedSafetyAction action = EnforcedSafetyAction.builder()
                                                                .build();

        final Consumer<EeveeActionException> failureHandler = (e -> {
            log.warn("Failed to send help text as embed.", e);
        });

        if (args.getCommandShortName() == null) {
            String notice = Session.getSession().getConfiguration().readString("eevee.notice");

            if (notice.length() > 0) {
                notice += "\n\n";
            }

            embedBuilder.setTitle("Eevee Help Directory");
            embedBuilder.setDescription(notice);
            embedBuilder.appendDescription(String.format(
                "Type one of the commands below to see help information for a command.%n" +
                    "The bot prefix is `%s`.",
                prefix
            ));
            embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.defaultEmbedColorDecimal"));

            commandMapper.getBotCommandGroups()
                         .stream()
                         .filter(group -> group.canBeInvokedBy(event.getMember()))
                         .forEach(group -> {
                             embedBuilder.addField(
                                 group.getLabel(),
                                 String.format(
                                     "%s%n%s%n",
                                     group.getDescription(),
                                     group.getCommands()
                                          .stream()
                                          .filter(command -> command.canBeInvokedBy(event.getMember()))
                                          .map(command -> {
                                              return String.format(
                                                  "⎖ %s - `%s help %s`",
                                                  command.getLabel(),
                                                  prefix,
                                                  command.getShortLabel()
                                              );
                                          })
                                          .collect(Collectors.joining("\n"))
                                 ),
                                 false
                             );
                         });

            action.sendEmbedMessage(failureHandler, event.getChannelId(), embedBuilder.build());
        } else {
            Optional<Command> commandOptional = commandMapper.getBotCommands()
                                                             .stream()
                                                             .filter(
                                                                 c -> c.getShortLabel()
                                                                       .equals(args.getCommandShortName())
                                                             )
                                                             .findFirst();

            if (!commandOptional.isPresent()) {
                embedBuilder.setTitle("Oops! An error occurred.");
                embedBuilder.setDescription("Invalid command specified.");
                embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.errorEmbedColorDecimal"));

                action.sendEmbedMessage(failureHandler, event.getChannelId(), embedBuilder.build());
            } else if (!commandOptional.get().canBeInvokedBy(event.getMember())) {
                embedBuilder.setTitle("Oops! An error occurred.");
                embedBuilder.setDescription("You do not have access to this command.");
                embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.errorEmbedColorDecimal"));

                action.sendEmbedMessage(failureHandler, event.getChannelId(), embedBuilder.build());
            } else {
                Command command = commandOptional.get();

                embedBuilder.setTitle(String.format("Eevee Help: %s", command.getLabel()));
                embedBuilder.setDescription(command.getDescription());
                embedBuilder.addField(
                    "Usage",
                    String.format("```%s```", command.getArguments().toString()),
                    false
                );
                embedBuilder.addField(
                    "Example",
                    String.format("```%s```", command.getExample()),
                    false
                );
                embedBuilder.addField(
                    "Required Permissions",
                    Formatter.formatPermissions(command.getRequiredPermissions()),
                    true
                );
                embedBuilder.addField(
                    "Rate Limit",
                    Formatter.formatRateLimit(command.getRateLimiter()),
                    true
                );
                embedBuilder.setColor(
                    Session.getSession().getConfiguration().readInt("eevee.defaultEmbedColorDecimal")
                );

                action.sendEmbedMessage(failureHandler, event.getChannelId(), embedBuilder.build());
            }
        }
    }
}
