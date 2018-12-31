package xyz.eevee.eevee.bot.command.util.remind;

import com.google.common.collect.ImmutableList;
import common.util.TimeUtil;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.EmbedBuilder;
import xyz.eevee.eevee.action.EnforcedSafetyAction;
import xyz.eevee.eevee.bot.command.Command;
import xyz.eevee.eevee.bot.command.CommandArguments;
import xyz.eevee.eevee.bot.command.CommandGroup;
import xyz.eevee.eevee.parser.arguments.Argument;
import xyz.eevee.eevee.parser.arguments.ArgumentOptions;
import xyz.eevee.eevee.parser.arguments.Arguments;
import xyz.eevee.eevee.parser.arguments.LiteralArgument;
import xyz.eevee.eevee.parser.arguments.NumberArgument;
import xyz.eevee.eevee.parser.arguments.OrArgument;
import xyz.eevee.eevee.parser.arguments.StringArgument;
import xyz.eevee.eevee.parser.arguments.VariadicArgument;
import xyz.eevee.eevee.repository.model.Reminder;
import xyz.eevee.eevee.service.ReminderService;
import xyz.eevee.eevee.session.Session;
import xyz.eevee.eevee.util.Formatter;
import xyz.eevee.munchlax.NewMessageEvent;

import java.time.Instant;

@Log4j2
public class RemindCommand extends Command {
    public RemindCommand(CommandGroup commandGroup) {
        super(commandGroup);
    }

    @Override
    public String getShortLabel() {
        return "remind";
    }

    @Override
    public String getLabel() {
        return "Remind Me";
    }

    @Override
    public String getDescription() {
        return "Set a reminder to do something.";
    }

    @Override
    public String getExample() {
        return "ev remind me in 1 hour 30 minutes to go to bed";
    }

    @Override
    public Arguments<RemindCommandArguments> getArguments() {
        Argument[] argsArray = {
            new LiteralArgument("remind"),
            new LiteralArgument("me"),
            new LiteralArgument("in").withOptions(
                ArgumentOptions.builder()
                               .required(false).build()
            ),
            new Arguments<>(ImmutableList.of(
                new NumberArgument("days").withOptions(
                    ArgumentOptions.builder()
                                   .required(false)
                                   .defaultValue(0)
                                   .build()
                ),
                new OrArgument("daysLabel", ImmutableList.of("days", "day"))
            ), RemindCommandArguments.class).withOptions(
                ArgumentOptions.builder()
                               .required(false)
                               .build()
            ),
            new Arguments<>(ImmutableList.of(
                new NumberArgument("hours").withOptions(
                    ArgumentOptions.builder()
                                   .defaultValue(0)
                                   .build()
                ),
                new OrArgument("hoursLabel", ImmutableList.of("hours", "hour"))
            ), RemindCommandArguments.class).withOptions(
                ArgumentOptions.builder()
                               .required(false)
                               .build()
            ),
            new Arguments<>(ImmutableList.of(
                new NumberArgument("minutes").withOptions(
                    ArgumentOptions.builder()
                                   .defaultValue(0)
                                   .build()
                ),
                new OrArgument("minutesLabel", ImmutableList.of("minutes", "minute"))
            ), RemindCommandArguments.class).withOptions(
                ArgumentOptions.builder()
                               .required(false)
                               .build()
            ),
            new Arguments<>(ImmutableList.of(
                new NumberArgument("seconds").withOptions(
                    ArgumentOptions.builder()
                                   .defaultValue(0)
                                   .build()
                ),
                new OrArgument("secondsLabel", ImmutableList.of("seconds", "second"))
            ), RemindCommandArguments.class).withOptions(
                ArgumentOptions.builder()
                               .required(false)
                               .build()
            ),
            new LiteralArgument("to").withOptions(
                ArgumentOptions.builder()
                               .required(false).build()
            ),
            new VariadicArgument<StringArgument, String>("action", new StringArgument("foo"))
        };

        return new Arguments<>(ImmutableList.copyOf(argsArray), RemindCommandArguments.class);
    }

    @Override
    public void invoke(@NonNull NewMessageEvent event, @NonNull CommandArguments arguments) {
        RemindCommandArguments args = (RemindCommandArguments) arguments;
        String remindAction = String.join(" ", args.getAction());
        long milli = TimeUtil.dhmsToMilli(args.getDays(), args.getHours(), args.getMinutes(), args.getSeconds());
        Instant now = Instant.now();

        Reminder reminder = Reminder.builder()
                                    .userTag(Formatter.formatTag(event.getAuthor()))
                                    .userId(event.getAuthor().getId())
                                    .reminder(remindAction)
                                    .remindAt(now.plusMillis(milli))
                                    .build();

        log.info("Adding new reminder to reminder datastore.");

        Session.getSession().getReminderDataRepository().add(reminder);

        log.info("Successfully added new reminder to reminder datastore.");

        ReminderService.createInstance(reminder).start();

        log.debug(String.format("Spawned new reminder thread for %s.", Formatter.formatTag(event.getAuthor())));

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Okay. I will remind you to...");
        embedBuilder.setDescription(remindAction);
        embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.defaultEmbedColorDecimal"));

        EnforcedSafetyAction.builder()
                            .build()
                            .sendEmbedMessage(e -> {
                                log.warn("Failed to send reminder creation message as embed.", e);
                            }, event.getChannelId(), embedBuilder.build());
    }
}
