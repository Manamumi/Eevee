package xyz.eevee.eevee.util;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import xyz.eevee.eevee.session.Session;
import xyz.eevee.eevee.target.DeveloperOnlyTargetLock;
import xyz.eevee.munchlax.Member;

public class StatsUtil {
    private static int OFFLINE_PING = -1;

    public static MessageEmbed createStatsEmbed(
        String thumbnailUrl,
        String uptimeString,
        int numberOfServers,
        int numberOfMembers,
        int ping,
        int apiPing,
        int coffeePing,
        int insidePing,
        Member member
    ) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Eevee Stats");
        embedBuilder.setThumbnail(thumbnailUrl);
        embedBuilder.addField("Uptime", uptimeString, false);
        embedBuilder.addField("Guild Count", Integer.toString(numberOfServers), true);
        embedBuilder.addField("Member Count", Integer.toString(numberOfMembers), true);
        embedBuilder.addField("WebSocket Ping", ping + "ms", true);
        embedBuilder.addField("API Ping", apiPing == OFFLINE_PING ? "Checking..." : apiPing + "ms", true);
        embedBuilder.addField(
            "Coffee Ping",
            formatServicePing(coffeePing),
            true
        );
        embedBuilder.addField(
            "Inside Ping",
            formatServicePing(insidePing),
            true
        );
        embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.defaultEmbedColorDecimal"));

        DeveloperOnlyTargetLock targetLock = new DeveloperOnlyTargetLock();

        if (targetLock.check(member)) {
            if (coffeePing == OFFLINE_PING) {
                embedBuilder.appendDescription("Coffee is currently down. Please check Inside for any ongoing SEVs.");
            }

            if (insidePing == OFFLINE_PING) {
                embedBuilder.appendDescription(
                    "Inside is currently down. Please check the Eevee server for help on what to do."
                );
            }
        }

        return embedBuilder.build();
    }

    private static String formatServicePing(int ping) {
        if (ping > OFFLINE_PING) {
            return String.format("%sms", ping);
        }
        return "Offline";
    }
}
