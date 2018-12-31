package xyz.eevee.eevee.bot.command;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import xyz.eevee.eevee.parser.Tokenizer;
import xyz.eevee.eevee.parser.arguments.Arguments;
import xyz.eevee.eevee.target.GenericAllPassTargetLock;
import xyz.eevee.eevee.target.TargetLock;
import xyz.eevee.eevee.util.PermissionUtil;
import xyz.eevee.munchlax.Member;
import xyz.eevee.munchlax.NewMessageEvent;

import java.util.List;
import java.util.Optional;

@Log4j2
public abstract class CommandGroup implements Module {
    public TargetLock getTargetLock() {
        return new GenericAllPassTargetLock();
    }

    public boolean canBeInvokedBy(@NonNull Member member) {
        if (PermissionUtil.isBotOwner(member)) {
            return true;
        }

        return getTargetLock().check(member);
    }

    public abstract List<Command> getCommands();

    public Optional<Command> getCommand(
        @NonNull Tokenizer tokenizer,
        @NonNull NewMessageEvent event
    ) {
        for (Command command : getCommands()) {
            Arguments<? extends CommandArguments> commandArguments = command.getArguments();

            if (commandArguments.isValid(tokenizer, event)) {
                tokenizer.reset();
                return Optional.of(command);
            }
            tokenizer.reset();
        }

        return Optional.empty();
    }
}
