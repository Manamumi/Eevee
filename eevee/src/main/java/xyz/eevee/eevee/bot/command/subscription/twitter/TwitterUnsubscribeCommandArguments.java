package xyz.eevee.eevee.bot.command.subscription.twitter;

import lombok.Getter;
import xyz.eevee.eevee.bot.command.CommandArguments;

public class TwitterUnsubscribeCommandArguments extends CommandArguments {
    @Getter
    private String user;
}
