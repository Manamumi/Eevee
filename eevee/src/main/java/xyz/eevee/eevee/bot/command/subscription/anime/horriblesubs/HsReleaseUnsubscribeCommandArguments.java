package xyz.eevee.eevee.bot.command.subscription.anime.horriblesubs;

import lombok.Getter;
import xyz.eevee.eevee.bot.command.CommandArguments;

public class HsReleaseUnsubscribeCommandArguments extends CommandArguments {
    @Getter
    private String animeName;

    @Getter
    private String quality;
}
