package xyz.eevee.eevee.bot.command.subscription.anime.nyaa;

import lombok.Getter;
import xyz.eevee.eevee.bot.command.CommandArguments;

public class NyaaReleaseUnsubscribeCommandArguments extends CommandArguments {
    @Getter
    private String subber;
    @Getter
    private String animeName;
    @Getter
    private String quality;
}
