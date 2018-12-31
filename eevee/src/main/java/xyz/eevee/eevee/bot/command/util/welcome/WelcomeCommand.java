package xyz.eevee.eevee.bot.command.util.welcome;

import com.google.common.collect.ImmutableList;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import xyz.eevee.eevee.action.EnforcedSafetyAction;
import xyz.eevee.eevee.bot.command.Command;
import xyz.eevee.eevee.bot.command.CommandArguments;
import xyz.eevee.eevee.bot.command.CommandGroup;
import xyz.eevee.eevee.parser.arguments.Argument;
import xyz.eevee.eevee.parser.arguments.Arguments;
import xyz.eevee.eevee.parser.arguments.LiteralArgument;
import xyz.eevee.eevee.parser.arguments.StringArgument;
import xyz.eevee.eevee.parser.arguments.VariadicArgument;
import xyz.eevee.eevee.repository.model.Guild;
import xyz.eevee.eevee.session.Session;
import xyz.eevee.eevee.util.DeprecateWithPorygonUtil;
import xyz.eevee.munchlax.GuildMemberJoinEvent;
import xyz.eevee.munchlax.NewMessageEvent;
import xyz.eevee.munchlax.Permission;

import java.util.List;
import java.util.Optional;

@Log4j2
public class WelcomeCommand extends Command {
    public WelcomeCommand(CommandGroup commandGroup) {
        super(commandGroup);
    }

    @Override
    public String getShortLabel() {
        return "welcome.set";
    }

    @Override
    public String getLabel() {
        return "Set Welcome Message";
    }

    @Override
    public String getDescription() {
        return "Allows you to set a custom welcome message for when a new member joins your guild.";
    }

    @Override
    public String getExample() {
        return "ev welcome set Hello {name}! Welcome to {channel} of {guild}, my secret lair.";
    }

    @Override
    public List<Permission> getRequiredPermissions() {
        return ImmutableList.of(Permission.MANAGE_SERVER);
    }

    @Override
    public Arguments<WelcomeCommandArguments> getArguments() {
        Argument[] argsArray = {
            new LiteralArgument("welcome"),
            new LiteralArgument("set"),
            new VariadicArgument<StringArgument, String>("message", new StringArgument("test"))
        };

        return new Arguments<>(ImmutableList.copyOf(argsArray), WelcomeCommandArguments.class);
    }

    @Override
    public void invoke(@NonNull NewMessageEvent event, @NonNull CommandArguments arguments) {
        WelcomeCommandArguments args = (WelcomeCommandArguments) arguments;
        String message = String.join(" ", args.getMessage());
        TextChannel textChannel = DeprecateWithPorygonUtil.getTextChannelById(event.getChannelId());

        Optional<Guild> guildOptional = Session.getSession()
                                               .getGuildDataRepository()
                                               .get(
                                                   DeprecateWithPorygonUtil.getGuildByChannel(event.getChannelId()).getId()
                                               );

        if (guildOptional.isPresent()) {
            Guild guild = guildOptional.get();
            guild.setWelcomeMessage(message);
            guild.setWelcomeChannelId(textChannel.getId());

            Session.getSession().getGuildDataRepository().update(guild);

            log.info(String.format("Updated welcome message for guild " +
                    "%s(%s) for the %s channel. ", guild.getGuildName(),
                guild.getServerId(),
                guild.getWelcomeChannelId()
            ));
        } else {
            Guild guild = Guild.builder()
                               .serverId(DeprecateWithPorygonUtil.getGuildByChannel(event.getChannelId()).getId())
                               .welcomeChannelId(textChannel.getId())
                               .guildName(DeprecateWithPorygonUtil.getGuildByChannel(event.getChannelId()).getName())
                               .welcomeMessage(message)
                               .build();

            log.info(String.format("Adding %s(%s) to guild datastore.", guild.getGuildName(),
                guild.getServerId()
            ));

            Session.getSession().getGuildDataRepository().add(guild);

            log.info(String.format("Sucessfully added %s(%s) to guild datastore.", guild.getGuildName(),
                guild.getServerId()
            ));
        }

        message = message.replace("{guild}", DeprecateWithPorygonUtil.getGuildByChannel(event.getChannelId()).getName());
        message = message.replace("{channel}", textChannel.getName());

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.defaultEmbedColorDecimal"));
        embedBuilder.setTitle("Welcome Message Set:");
        embedBuilder.setDescription(message);

        EnforcedSafetyAction.builder()
                            .build()
                            .sendEmbedMessage(e -> {
                                log.warn("Failed to send welcome set confirmation.", e);
                            }, textChannel.getId(), embedBuilder.build());
    }

    public static void welcomeListener(GuildMemberJoinEvent event) {
        Optional<Guild> guildOptional = Session.getSession()
                                               .getGuildDataRepository()
                                               .get(
                                                   event.getMember().getGuild().getId()
                                               );

        if (!guildOptional.isPresent() || guildOptional.get().getWelcomeMessage() == null) {
            return;
        }

        Guild guild = guildOptional.get();
        String message = guild.getWelcomeMessage();
        TextChannel textChannel = DeprecateWithPorygonUtil.getTextChannelById(guild.getWelcomeChannelId());
        if (textChannel == null) {
            textChannel = DeprecateWithPorygonUtil.getGuildById(event.getMember().getGuild().getId()).getDefaultChannel();
        }

        message = message.replace(
            "{guild}",
            DeprecateWithPorygonUtil.getGuildById(event.getMember().getGuild().getId()).getName()
        ).replace(
            "{name}",
           event.getMember().getEffectiveName()
        ).replace("{channel}", textChannel.getName());

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.defaultEmbedColorDecimal"));
        embedBuilder.setDescription(message);

        EnforcedSafetyAction.builder()
                            .build()
                            .sendEmbedMessage(e -> {
                                log.warn("Failed to send welcome message.", e);
                            }, textChannel.getId(), embedBuilder.build());
    }
}