package xyz.eevee.eevee.bot.command.fun.pokemon;

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
import xyz.eevee.eevee.session.Session;
import xyz.eevee.munchlax.NewMessageEvent;

import java.util.List;
import java.util.Random;

@Log4j2
public class BestPokemonCommand extends Command {
    public BestPokemonCommand(CommandGroup commandGroup) {
        super(commandGroup);
    }

    @Override
    public String getShortLabel() {
        return "pokemon.best";
    }

    @Override
    public String getLabel() {
        return "What's the best Pokemon?";
    }

    @Override
    public String getDescription() {
        return "Returns a random picture of Eevee!";
    }

    @Override
    public Arguments<? extends CommandArguments> getArguments() {
        return new Arguments<>(ImmutableList.of(
            new LiteralArgument("best"),
            new LiteralArgument("pokemon?")
        ), BestPokemonCommandArguments.class);
    }

    @Override
    public String getExample() {
        return "ev best pokemon?";
    }

    @Override
    public void invoke(@NonNull NewMessageEvent event, @NonNull CommandArguments arguments) {
        List<String> eevees = Session.getSession().getConfiguration().readStringList("eevee.eevees");

        Random random = new Random();
        int index = random.nextInt(eevees.size());

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("It's Eevee!");
        embedBuilder.setImage(eevees.get(index));
        embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.defaultEmbedColorDecimal"));

        EnforcedSafetyAction.builder()
                            .build()
                            .sendEmbedMessage(e -> {
                                log.warn("Failed to send best pokemon image as embed.", e);
                            }, event.getChannelId(), embedBuilder.build());
    }
}
