package xyz.eevee.eevee.bot.command.util.jisho;

import lombok.Getter;
import xyz.eevee.eevee.bot.command.CommandArguments;

public class JishoCommandArguments extends CommandArguments {
    @Getter
    private String searchQuery;
}
