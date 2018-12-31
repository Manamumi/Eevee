package xyz.eevee.eevee.bot.command.fun;

import com.google.common.collect.ImmutableList;
import xyz.eevee.eevee.bot.command.Command;
import xyz.eevee.eevee.bot.command.CommandGroup;
import xyz.eevee.eevee.bot.command.fun.attack.AttackCommand;
import xyz.eevee.eevee.bot.command.fun.emote.EmoteCommand;
import xyz.eevee.eevee.bot.command.fun.ghost.GhostCommand;
import xyz.eevee.eevee.bot.command.fun.ghost.GhostToggleCommand;
import xyz.eevee.eevee.bot.command.fun.pokemon.BestPokemonCommand;

import java.util.List;

public class FunCommandGroup extends CommandGroup {
    private final List<Command> commands = ImmutableList.of(
        new EmoteCommand(this),
        new GhostCommand(this),
        new GhostToggleCommand(this),
        new BestPokemonCommand(this),
        new AttackCommand(this)
    );

    @Override
    public String getShortLabel() {
        return "fun";
    }

    @Override
    public String getLabel() {
        return ":game_die: Fun Commands";
    }

    @Override
    public String getDescription() {
        return "Commands related to fun things like ghosting and attacking other people.";
    }

    @Override
    public List<Command> getCommands() {
        return commands;
    }
}
