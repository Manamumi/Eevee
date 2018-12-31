// SANITY_IGNORE_ENFORCED_SAFETY

package xyz.eevee.eevee.bot.command.dev.f12;

import com.google.common.collect.ImmutableList;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.EmbedBuilder;
import xyz.eevee.eevee.action.EnforcedSafetyAction;
import xyz.eevee.eevee.bot.command.Command;
import xyz.eevee.eevee.bot.command.CommandArguments;
import xyz.eevee.eevee.bot.command.CommandGroup;
import xyz.eevee.eevee.parser.arguments.Arguments;
import xyz.eevee.eevee.parser.arguments.LiteralArgument;
import xyz.eevee.eevee.parser.arguments.StringArgument;
import xyz.eevee.eevee.parser.arguments.VariadicArgument;
import xyz.eevee.eevee.session.Session;
import xyz.eevee.eevee.util.Formatter;
import xyz.eevee.munchlax.NewMessageEvent;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
public class F12Command extends Command {
    private final ScriptEngineManager engineManager;
    private final Map<String, ScriptEngine> engineSessions;

    public F12Command(CommandGroup commandGroup) {
        super(commandGroup);
        engineManager = new ScriptEngineManager();
        engineSessions = new HashMap<>();
    }

    @Override
    public String getShortLabel() {
        return "f12";
    }

    @Override
    public String getLabel() {
        return "~~Chrome F12~~ Interactive ~~Console~~ Shell";
    }

    @Override
    public String getDescription() {
        return "Starts an interactive shell for mucking around with Eevee's internals. " +
            "Can only be used by Eevee developers.";
    }

    @Override
    public String getExample() {
        return "ev f12 event.getChannel().sendMessage('owo').queue()";
    }

    @Override
    public Arguments<F12CommandArguments> getArguments() {
        return new Arguments<>(ImmutableList.of(
            new LiteralArgument("f12"),
            new VariadicArgument<StringArgument, String>("commandTokens", new StringArgument("foo"))
        ), F12CommandArguments.class);
    }

    @Override
    public void invoke(@NonNull NewMessageEvent event, @NonNull CommandArguments arguments) {
        F12CommandArguments args = (F12CommandArguments) arguments;
        String commandString = args.getCommandTokens().stream().collect(Collectors.joining(" "));
        commandString = commandString.replaceAll("^```|```$", "");

        ScriptEngine jsEngine;

        String userId = Formatter.formatTag(event.getAuthor());

        log.info(String.format("User %s has invoked: %s", userId, commandString));

        EnforcedSafetyAction action = EnforcedSafetyAction.builder()
                                                          .build();

        if (commandString.equalsIgnoreCase("exit")) {
            if (engineSessions.containsKey(event.getAuthor().getId())) {
                engineSessions.remove(event.getAuthor().getId());
            }

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Interactive Shell");
            embedBuilder.setDescription("_Exited interactive shell._");
            embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.defaultEmbedColorDecimal"));

            action.sendEmbedMessage(e -> {
                log.warn("Failed to send message embed for F12 command.", e);
            }, event.getChannelId(), embedBuilder.build());
            return;
        }

        if (engineSessions.containsKey(event.getAuthor().getId())) {
            log.info(String.format("Found existing engine session for user %s.", userId));
            jsEngine = engineSessions.get(event.getAuthor().getId());
        } else {
            log.info(String.format("Could not find existing engine session for user %s. Creating new engine.", userId));
            jsEngine = engineManager.getEngineByName("nashorn");
            jsEngine.put("session", Session.getSession());

            engineSessions.put(event.getAuthor().getId(), jsEngine);
        }

        jsEngine.put("event", event);

        try {
            Object result = jsEngine.eval(commandString);

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Interactive Shell");
            embedBuilder.setDescription(result == null ? "_No Output_" : result.toString());
            embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.defaultEmbedColorDecimal"));

            action.sendEmbedMessage(e -> {
                log.warn("Failed to send message embed for F12 command.", e);
            }, event.getChannelId(), embedBuilder.build());
        } catch (ScriptException e) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Interactive Shell");
            embedBuilder.setDescription(e.getMessage());
            embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.errorEmbedColorDecimal"));

            action.sendEmbedMessage(ex -> {
                log.warn("Failed to send message embed for F12 command.", ex);
            }, event.getChannelId(), embedBuilder.build());
        }
    }
}
