package xyz.eevee.eevee.bot.command.subscription.manga.mangadex;

import lombok.Getter;
import xyz.eevee.eevee.bot.command.CommandArguments;

public class MangaDexReleaseSubscribeCommandArguments extends CommandArguments {
    @Getter
    private String mangaName;
    @Getter
    private String scanlationGroup;
}
