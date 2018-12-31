package xyz.eevee.eevee.bot.command.util.welcome;

import lombok.Getter;
import xyz.eevee.eevee.bot.command.CommandArguments;

import java.util.List;

public class WelcomeCommandArguments extends CommandArguments {
    @Getter
    private List<String> message;
}
