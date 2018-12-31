package xyz.eevee.munchlax;

import common.util.TimeUtil;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.stream.Collectors;

public class Marshal {
    public static NewMessageEvent marshalMessageEvent(MessageReceivedEvent event) {
        NewMessageEvent.Builder builder = NewMessageEvent.newBuilder()
                                                         .setId(event.getMessageId())
                                                         .setContent(event.getMessage().getContentRaw())
                                                         .setTimestamp(
                                                             TimeUtil.offsetDateTimeToIso8601(event.getMessage().getCreationTime())
                                                         )
                                                         .setAuthor(marshalUser(event.getAuthor()))
                                                         .setChannelId(event.getChannel().getId())
                                                         .setRenderedContent(event.getMessage().getContentDisplay());

        if (event.getGuild() != null) {
            builder.setGuild(marshalGuild(event.getGuild()));
        }

        if (event.getMember() != null) {
            builder.setMember(marshalMember(event.getMember()));
        }

        if (event.getMessage().getMentionedMembers() != null) {
            builder.addAllMentions(marshalMembers(event.getMessage().getMentionedMembers()));
        }

        return builder.build();
    }

    public static GuildMemberJoinEvent marshallGuildJoinEvent(
        net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent event
    ) {
        return GuildMemberJoinEvent.newBuilder()
                                   .setMember(marshalMember(event.getMember()))
                                   .setGuild(marshalGuild(event.getGuild()))
                                   .setTimestamp(TimeUtil.offsetDateTimeToIso8601(event.getMember().getJoinDate()))
                                   .build();
    }

    public static Member marshalMember(net.dv8tion.jda.core.entities.Member member) {
        Member.Builder builder = Member.newBuilder()
                                       .setUser(marshalUser(member.getUser()))
                                       .setGuild(marshalGuild(member.getGuild()))
                                       .setJoinedAt(TimeUtil.offsetDateTimeToIso8601(member.getJoinDate()))
                                       .addAllPermissions(marshalPermissions(member.getPermissions()))
                                       .setEffectiveName(member.getEffectiveName());

        if (member.getNickname() != null) {
            builder.setNick(member.getNickname());
        }

        return builder.build();
    }

    public static List<Member> marshalMembers(List<net.dv8tion.jda.core.entities.Member> members) {
        return members.stream().map(Marshal::marshalMember).collect(Collectors.toList());
    }

    public static User marshalUser(net.dv8tion.jda.core.entities.User user) {
        User.Builder builder = User.newBuilder()
                                   .setAvatar(user.getEffectiveAvatarUrl())
                                   .setDiscriminator(user.getDiscriminator())
                                   .setUsername(user.getName())
                                   .setId(user.getId());

        if (user.getAvatarUrl() != null) {
            builder.setAvatar(user.getAvatarUrl());
        }

        return builder.build();
    }

    public static Permission marshalPermission(net.dv8tion.jda.core.Permission permission) {
        // JDA represents permissions as a bitmask.
        return Permission.forNumber(permission.getOffset());
    }

    public static List<Permission> marshalPermissions(List<net.dv8tion.jda.core.Permission> permissions) {
        return permissions.stream().map(Marshal::marshalPermission).collect(Collectors.toList());
    }

    public static Guild marshalGuild(net.dv8tion.jda.core.entities.Guild guild) {
        Guild.Builder builder = Guild.newBuilder()
                    .setId(guild.getId())
                    .setCreatedAt(TimeUtil.offsetDateTimeToIso8601(guild.getCreationTime()))
                    .setName(guild.getName())
                    .addAllMembers(
                        guild.getMembers()
                             .stream()
                             .map(m -> m.getUser().getId())
                             .collect(Collectors.toList())
                    );

        if (guild.getIconUrl() != null) {
            builder.setIconUrl(guild.getIconUrl());
        }

        return builder.build();
    }
}
