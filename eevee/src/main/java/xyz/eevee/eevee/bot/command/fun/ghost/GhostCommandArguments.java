package xyz.eevee.eevee.bot.command.fun.ghost;

import lombok.Getter;
import xyz.eevee.eevee.bot.command.CommandArguments;
import xyz.eevee.munchlax.Member;
import xyz.eevee.munchlax.User;

public class GhostCommandArguments extends CommandArguments {
    @Getter
    private Member ghostee;
}
