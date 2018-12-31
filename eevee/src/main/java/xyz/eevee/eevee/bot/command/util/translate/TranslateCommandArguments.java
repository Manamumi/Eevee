package xyz.eevee.eevee.bot.command.util.translate;

import lombok.Getter;
import xyz.eevee.eevee.bot.command.CommandArguments;

import java.util.List;

public class TranslateCommandArguments extends CommandArguments {
    @Getter
    private String targetLanguage;
    @Getter
    private List<String> sourceText;
}
