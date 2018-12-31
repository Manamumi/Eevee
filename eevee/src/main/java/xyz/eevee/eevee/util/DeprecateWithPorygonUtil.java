package xyz.eevee.eevee.util;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import xyz.eevee.eevee.session.Session;
import xyz.eevee.munchlax.Member;
import xyz.eevee.munchlax.Permission;

import java.util.List;

public class DeprecateWithPorygonUtil {
    public static TextChannel getTextChannelById(String channelId) {
        return Session.getSession()
                      .getJdaClient()
                      .getTextChannelById(channelId);
    }

    public static Guild getGuildByChannel(String channelId) {
        return getTextChannelById(channelId).getGuild();
    }

    public static Guild getGuildById(String guildId) {
        return Session.getSession()
                      .getJdaClient()
                      .getGuildById(guildId);
    }

    public static boolean hasPermission(Member member, List<Permission> permissions) {
        for (Permission permission : permissions) {
            if (!member.getPermissionsList().contains(permission)) {
                return false;
            }
        }

        return true;
    }

    public static net.dv8tion.jda.core.entities.User getUserById(String userId) {
        return Session.getSession().getJdaClient().getUserById(userId);
    }
}
