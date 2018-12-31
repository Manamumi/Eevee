package xyz.eevee.eevee.bot.command.util.avatar;

import lombok.Getter;
import xyz.eevee.eevee.bot.command.CommandArguments;
import xyz.eevee.munchlax.User;

public class AvatarCommandArguments extends CommandArguments {
    @Getter
    private User mentionedUser;
}
