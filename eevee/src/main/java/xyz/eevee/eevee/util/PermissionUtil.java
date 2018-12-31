package xyz.eevee.eevee.util;

import lombok.NonNull;
import xyz.eevee.eevee.session.Session;
import xyz.eevee.munchlax.Member;
import xyz.eevee.munchlax.User;

public class PermissionUtil {
    /**
     * Given a Discord user, check whether or not they are the bot owner. The bot owner is defined in the bot's
     * configuration.
     *
     * @param user A Discord user.
     * @return A boolean with true or false depending on whether or not the given user is the bot owner.
     */
    public static boolean isBotOwner(@NonNull User user) {
        return isBotOwner(user.getId());
    }

    public static boolean isBotOwner(@NonNull Member member) {
        return isBotOwner(member.getUser().getId());
    }

    /**
     * Given a Discord user ID, check whether or not it belongs to the bot owner. The bot owner is defined in the bot's
     * configuration.
     *
     * @param id A Discord user ID.
     * @return A boolean with true or false depending on whether or not the given ID belongs to the bot owner.
     */
    public static boolean isBotOwner(@NonNull String id) {
        return id.equals(Session.getSession().getConfiguration().readString("eevee.botOwnerId"));
    }
}
