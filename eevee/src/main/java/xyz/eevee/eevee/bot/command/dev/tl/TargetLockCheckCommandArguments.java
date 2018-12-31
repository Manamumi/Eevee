package xyz.eevee.eevee.bot.command.dev.tl;

import lombok.Getter;
import xyz.eevee.eevee.bot.command.CommandArguments;
import xyz.eevee.munchlax.Member;

public class TargetLockCheckCommandArguments extends CommandArguments {
    @Getter
    private String shortLabel;
    @Getter
    private Member user;
}
