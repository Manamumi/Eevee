package xyz.eevee.eevee.bot.command.fun.attack;

import lombok.Getter;
import xyz.eevee.eevee.bot.command.CommandArguments;
import xyz.eevee.munchlax.Member;

public class AttackCommandArguments extends CommandArguments {
    @Getter
    Member victim;
}
