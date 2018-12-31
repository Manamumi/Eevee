package xyz.eevee.eevee.bot.command.fun.emote;

import com.google.common.collect.ImmutableList;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import xyz.eevee.eevee.action.EnforcedSafetyAction;
import xyz.eevee.eevee.bot.command.Command;
import xyz.eevee.eevee.bot.command.CommandArguments;
import xyz.eevee.eevee.bot.command.CommandGroup;
import xyz.eevee.eevee.parser.arguments.ArgumentOptions;
import xyz.eevee.eevee.parser.arguments.Arguments;
import xyz.eevee.eevee.parser.arguments.LiteralArgument;
import xyz.eevee.eevee.parser.arguments.StringArgument;
import xyz.eevee.eevee.session.Session;
import xyz.eevee.munchlax.NewMessageEvent;

import java.util.List;
import java.util.Random;

@Log4j2
public class EmoteCommand extends Command {
    public EmoteCommand(CommandGroup commandGroup) {
        super(commandGroup);
    }

    @Override
    public String getShortLabel() {
        return "emote";
    }

    @Override
    public String getLabel() {
        return "Random Eevee Emote";
    }

    @Override
    public String getDescription() {
        return "Shows a Eevee emote. If no emote is specified then a random one is shown.";
    }

    @Override
    public String getExample() {
        return "ev emote eeveesip";
    }

    @Override
    public Arguments<? extends CommandArguments> getArguments() {
        return new Arguments<>(ImmutableList.of(
            new LiteralArgument("emote"),
            new StringArgument("emote").withOptions(
                ArgumentOptions.builder()
                               .required(false)
                               .build()
            )
        ), EmoteCommandArguments.class);
    }

    @Override
    public void invoke(@NonNull NewMessageEvent event, @NonNull CommandArguments arguments) {
        EmoteCommandArguments args = (EmoteCommandArguments) arguments;

        List<String> eevees = Session.getSession().getConfiguration().readStringList("eevee.emote");

        EnforcedSafetyAction action = EnforcedSafetyAction.builder()
                                                          .build();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.defaultEmbedColorDecimal"));

        if (args.getEmote() == null) {
            Random random = new Random();
            int index = random.nextInt(eevees.size());
            String emote = eevees.get(index);
            String url = emote.split(" ")[1];
            embedBuilder.setImage(url);
            action.sendEmbedMessage(e -> {
                log.warn("Failed to send emote as message embed.", e);
            }, event.getChannelId(), embedBuilder.build());
        } else {
            // DEPENDS: <redacted>
            for (String emote : eevees) {
                String[] parts = emote.split(" ");
                String name = parts[0];
                String emoteUrl = parts[1];

                if (name.equalsIgnoreCase(args.getEmote())) {
                    embedBuilder.setImage(emoteUrl);
                    action.sendEmbedMessage(e -> {
                        log.warn("Failed to send emote as message embed.", e);
                    }, event.getChannelId(), embedBuilder.build());
                    break;
                }
            }
        }
    }
}
