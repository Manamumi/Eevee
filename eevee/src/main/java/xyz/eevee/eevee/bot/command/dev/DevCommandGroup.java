package xyz.eevee.eevee.bot.command.dev;

import com.google.common.collect.ImmutableList;
import xyz.eevee.eevee.bot.command.Command;
import xyz.eevee.eevee.bot.command.CommandGroup;
import xyz.eevee.eevee.bot.command.dev.build.BuildInfoCommand;
import xyz.eevee.eevee.bot.command.dev.coffee.BrewCommand;
import xyz.eevee.eevee.bot.command.dev.f12.F12Command;
import xyz.eevee.eevee.bot.command.dev.tl.TargetLockCheckCommand;
import xyz.eevee.eevee.target.DeveloperOnlyTargetLock;
import xyz.eevee.eevee.target.TargetLock;

import java.util.List;

public class DevCommandGroup extends CommandGroup {
    private final List<Command> commands = ImmutableList.of(
        new BuildInfoCommand(this),
        new BrewCommand(this),
        new F12Command(this),
        new TargetLockCheckCommand(this)
    );

    @Override
    public String getShortLabel() {
        return "dev";
    }

    @Override
    public String getLabel() {
        return ":computer: Developer Commands";
    }

    @Override
    public String getDescription() {
        return "Commands that are only available to Eevee developers.";
    }

    @Override
    public TargetLock getTargetLock() {
        return new DeveloperOnlyTargetLock();
    }

    @Override
    public List<Command> getCommands() {
        return commands;
    }
}
