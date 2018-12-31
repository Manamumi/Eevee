package xyz.eevee.eevee.bot.command.util.welcome;

import com.google.common.collect.ImmutableList;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.EmbedBuilder;
import xyz.eevee.eevee.action.EnforcedSafetyAction;
import xyz.eevee.eevee.bot.command.Command;
import xyz.eevee.eevee.bot.command.CommandArguments;
import xyz.eevee.eevee.bot.command.CommandGroup;
import xyz.eevee.eevee.parser.arguments.Argument;
import xyz.eevee.eevee.parser.arguments.Arguments;
import xyz.eevee.eevee.parser.arguments.LiteralArgument;
import xyz.eevee.eevee.repository.model.Guild;
import xyz.eevee.eevee.session.Session;
import xyz.eevee.eevee.util.DeprecateWithPorygonUtil;
import xyz.eevee.munchlax.NewMessageEvent;
import xyz.eevee.munchlax.Permission;

import java.util.List;
import java.util.Optional;

@Log4j2
public class RemoveWelcomeCommand extends Command {
    public RemoveWelcomeCommand(CommandGroup commandgroup) {
        super(commandgroup);
    }

    @Override
    public String getShortLabel() {
        return "welcome.remove";
    }

    @Override
    public String getLabel() {
        return "Remove Welcome Message";
    }

    @Override
    public String getDescription() {
        return "Allows you to remove the welcome message currently set for your guild.";
    }

    @Override
    public String getExample() {
        return "ev welcome remove";
    }

    @Override
    public List<Permission> getRequiredPermissions() {
        return ImmutableList.of(Permission.MANAGE_SERVER);
    }

    @Override
    public Arguments<RemoveWelcomeCommandArguments> getArguments() {
        Argument[] argsArray = {
            new LiteralArgument("welcome"),
            new LiteralArgument("remove")
        };

        return new Arguments<>(ImmutableList.copyOf(argsArray), RemoveWelcomeCommandArguments.class);
    }

    @Override
    public void invoke(@NonNull NewMessageEvent event, @NonNull CommandArguments arguments) {
        boolean fail = false;
        Optional<Guild> guildOptional = Session.getSession()
                                               .getGuildDataRepository()
                                               .get(
                                                   DeprecateWithPorygonUtil.getGuildByChannel(event.getChannelId())
                                                                           .getId()
                                               );

        if (!guildOptional.isPresent()) {
            fail = true;
        } else {
            Guild guild = guildOptional.get();

            if (guild.getWelcomeMessage() == null) {
                fail = true;
            } else {
                guild.setWelcomeMessage(null);
                guild.setWelcomeChannelId(null);

                log.info(
                    String.format(
                        "Deleting welcome message for guild, %s(%s).",
                        DeprecateWithPorygonUtil.getGuildByChannel(event.getChannelId()).getName(),
                        guild.getServerId()
                    )
                );

                Session.getSession().getGuildDataRepository().update(guild);

                log.info(String.format("Successfully deleted welcome message for guild, %s(%s).",
                    DeprecateWithPorygonUtil.getGuildByChannel(event.getChannelId()).getName(),
                    guild.getServerId()
                ));
            }
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();

        if (fail) {
            embedBuilder.setTitle("Unable to Remove Welcome");
            embedBuilder.setDescription(String.format("There is no welcome message currently set for guild %s.",
                DeprecateWithPorygonUtil.getGuildByChannel(event.getChannelId()).getName()
            ));
            embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.errorEmbedColorDecimal"));
        } else {
            embedBuilder.setTitle("Welcome Removed");
            embedBuilder.setDescription(String.format("Deleted welcome message for guild %s.",
                DeprecateWithPorygonUtil.getGuildByChannel(event.getChannelId()).getName()
            ));
            embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.successEmbedColorDecimal"));
        }

        EnforcedSafetyAction.builder()
                            .build()
                            .sendEmbedMessage(e -> {
                                log.warn("Failed to send welcome message.", e);
                            }, event.getChannelId(), embedBuilder.build());
    }
}
