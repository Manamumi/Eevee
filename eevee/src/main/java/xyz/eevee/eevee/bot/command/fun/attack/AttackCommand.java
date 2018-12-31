package xyz.eevee.eevee.bot.command.fun.attack;

import com.google.common.collect.ImmutableList;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.EmbedBuilder;
import xyz.eevee.eevee.action.EnforcedSafetyAction;
import xyz.eevee.eevee.bot.command.Command;
import xyz.eevee.eevee.bot.command.CommandArguments;
import xyz.eevee.eevee.bot.command.CommandGroup;
import xyz.eevee.eevee.parser.arguments.Arguments;
import xyz.eevee.eevee.parser.arguments.LiteralArgument;
import xyz.eevee.eevee.parser.arguments.MemberArgument;
import xyz.eevee.eevee.session.Session;
import xyz.eevee.munchlax.Member;
import xyz.eevee.munchlax.NewMessageEvent;

import java.util.List;
import java.util.Random;

@Log4j2
public class AttackCommand extends Command {
    public AttackCommand(CommandGroup commandGroup) {
        super(commandGroup);
    }

    @Override
    public String getShortLabel() {
        return "attack";
    }

    @Override
    public String getLabel() {
        return "Attack Somebody";
    }

    @Override
    public String getDescription() {
        return "Make Eevee attack somebody.";
    }

    @Override
    public String getExample() {
        return "ev attack @Someone";
    }

    @Override
    public Arguments<? extends CommandArguments> getArguments() {
        return new Arguments<>(ImmutableList.of(
            new LiteralArgument("attack"),
            new MemberArgument("victim")
        ), AttackCommandArguments.class);
    }

    @Override
    public void invoke(NewMessageEvent event, CommandArguments arguments) {
        AttackCommandArguments args = (AttackCommandArguments) arguments;
        List<String> immunities = Session.getSession()
                                         .getConfiguration()
                                         .readStringList("attackImmunity");

        Member victim = args.getVictim();

        if (immunities.contains(victim.getUser().getId())) {
            victim = event.getMember();
        }

        List<String> attacks = Session.getSession()
                                      .getConfiguration()
                                      .readStringList("eeveeAttacks");

        List<String> attackPhrases = Session.getSession()
                                            .getConfiguration()
                                            .readStringList("attackPhrases");

        Random random = new Random();
        String attack = attacks.get(random.nextInt(attacks.size()));
        String attackPhrase = attackPhrases.get(random.nextInt(attackPhrases.size()));

        attackPhrase = attackPhrase.replace("{attack}", attack);
        attackPhrase = attackPhrase.replace("{victim}", victim.getEffectiveName());

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Eevee Attacks!");
        embedBuilder.setDescription(String.format("%s", attackPhrase));
        embedBuilder.setColor(
            Session.getSession().getConfiguration().readInt("eevee.defaultEmbedColorDecimal")
        );

        EnforcedSafetyAction.builder()
                            .build()
                            .sendEmbedMessage(e -> {
                                log.warn("Failed to send attack as embed.", e);
                            }, event.getChannelId(), embedBuilder.build());
    }
}
