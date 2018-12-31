package xyz.eevee.eevee.bot;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import xyz.eevee.eevee.bot.command.Command;
import xyz.eevee.eevee.bot.command.CommandGroup;
import xyz.eevee.eevee.parser.Tokenizer;
import xyz.eevee.munchlax.NewMessageEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Log4j2
public class CommandMapper {
    @Getter
    private List<CommandGroup> botCommandGroups;
    @Getter
    private List<Command> botCommands;

    public void addModule(@NonNull CommandGroup commandGroup) {
        if (botCommandGroups == null) {
            botCommandGroups = new LinkedList<>();
        }

        if (botCommands == null) {
            botCommands = new LinkedList<>();
        }

        botCommandGroups.add(commandGroup);

        for (Command command : commandGroup.getCommands()) {
            try {
                command.bootstrap();
                botCommands.add(command);
                log.info(String.format("Registered command: %s.", command.getShortLabel()));
            } catch (RuntimeException e) {
                log.error(String.format("Failed to bootstrap command: %s.", command.getShortLabel()), e);
                log.info(String.format("Skipping command: %s.", command.getShortLabel()));
            }
        }

        log.info(String.format("Registered commandGroup: %s.", commandGroup.getShortLabel()));
    }

    public Optional<Command> get(@NonNull Tokenizer tokenizer, @NonNull NewMessageEvent event) {
        for (CommandGroup commandGroup : botCommandGroups) {
            if (!commandGroup.canBeInvokedBy(event.getMember())) {
                continue;
            }

            Optional<Command> commandOptional = commandGroup.getCommand(tokenizer, event);

            if (!commandOptional.isPresent()) {
                continue;
            }

            Command command = commandOptional.get();

            if (checkCommand(command, event)) {
                return commandOptional;
            } else {
                break;
            }
        }

        return Optional.empty();
    }

    private boolean checkCommand(
        @NonNull Command command,
        @NonNull NewMessageEvent event
    ) {
        if (!command.canBeInvokedBy(event.getMember()) || command.isRateLimited(event.getAuthor())) {
            log.info(
                String.format(
                    "User %s#%s attempted to execute command \"%s\". User does not have permission " +
                        "or is rate-limited. User ID: %s",
                    event.getAuthor().getUsername(),
                    event.getAuthor().getDiscriminator(),
                    event.getContent(),
                    event.getAuthor().getId()
                )
            );
            return false;
        }

        return true;
    }
}
