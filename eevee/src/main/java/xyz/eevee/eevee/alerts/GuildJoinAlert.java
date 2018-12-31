package xyz.eevee.eevee.alerts;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import xyz.eevee.eevee.action.EnforcedSafetyAction;
import xyz.eevee.eevee.session.Session;

@Log4j2
public class GuildJoinAlert {
    public static void announce(@NonNull GuildJoinEvent event) {
        String announcementChannelId = Session.getSession()
                                              .getConfiguration()
                                              .readString("newServerJoinAnnouncementChannel");

        TextChannel announcementChannel = Session.getSession()
                                                 .getJdaClient()
                                                 .getTextChannelById(announcementChannelId);

        if (announcementChannel == null) {
            log.warn("The new server join announcement channel does not exist. Please update in Coffee and brew.");
            return;
        }

        Guild joinedGuild = event.getGuild();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(
            Session.getSession()
                   .getConfiguration()
                   .readInt("eevee.defaultEmbedColorDecimal")
        );
        embedBuilder.setTitle("New Server Joined");
        embedBuilder.addField("Server Name", joinedGuild.getName(), false);
        embedBuilder.addField("Server ID", joinedGuild.getId(), false);
        embedBuilder.addField("Member Count", Integer.toString(joinedGuild.getMembers().size()), false);

        EnforcedSafetyAction.builder()
                            .build()
                            .sendEmbedMessage(e -> {
                                log.warn("Failed to send new server notification.", e);
                            }, announcementChannelId, embedBuilder.build());

        String announcementText = String.format(
            "Eevee has joined a new server.%n```%nName: %s%nID: %s%nMember Count: %s%n```",
            joinedGuild.getName(),
            joinedGuild.getId(),
            // Do not include Eevee.
            joinedGuild.getMembers().size() - 1
        );

        log.info(announcementText);
    }
}
