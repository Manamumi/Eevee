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
import xyz.eevee.eevee.configuration.GlobalConfiguration;
import xyz.eevee.eevee.parser.arguments.Argument;
import xyz.eevee.eevee.parser.arguments.ArgumentOptions;
import xyz.eevee.eevee.parser.arguments.Arguments;
import xyz.eevee.eevee.parser.arguments.LiteralArgument;
import xyz.eevee.eevee.parser.arguments.MemberArgument;
import xyz.eevee.eevee.parser.arguments.NumberArgument;
import xyz.eevee.eevee.parser.arguments.OrArgument;
import xyz.eevee.eevee.parser.arguments.StringArgument;
import xyz.eevee.eevee.parser.arguments.VariadicArgument;
import xyz.eevee.eevee.repository.model.GenericStringList;
import xyz.eevee.eevee.repository.model.Reminder;
import xyz.eevee.eevee.service.ReminderService;
import xyz.eevee.eevee.session.Session;
import xyz.eevee.eevee.util.Formatter;
import xyz.eevee.munchlax.NewMessageEvent;
import xyz.eevee.munchlax.User;

import java.time.Instant;
import java.util.Optional;

@Log4j2
public class RemindSomebodyCommand extends Command {
    public RemindSomebodyCommand(CommandGroup commandGroup) {
        super(commandGroup);
    }

    @Override
    public String getShortLabel() {
        return "remind.somebody";
    }

    @Override
    public String getLabel() {
        return "Remind Somebody";
    }

    @Override
    public String getDescription() {
        return "Set a reminder for somebody to do something.";
    }

    @Override
    public String getExample() {
        return "ev remind @Someone in 1 hour 30 minutes to go to bed";
    }

    @Override
    public Arguments<? extends CommandArguments> getArguments() {
        Argument[] argsArray = {
            new LiteralArgument("remind"),
            new MemberArgument("person"),
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

        return new Arguments<>(ImmutableList.copyOf(argsArray), RemindSomebodyCommandArguments.class);
    }

    @Override
    public void invoke(@NonNull NewMessageEvent event, @NonNull CommandArguments arguments) {
        RemindSomebodyCommandArguments args = (RemindSomebodyCommandArguments) arguments;

        EnforcedSafetyAction action = EnforcedSafetyAction.builder()
                                                          .build();

        if (isOptedOut(args.getPerson().getUser())) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(
                String.format(
                    "Unable to Add Reminder for %s",
                    Formatter.formatTag(args.getPerson().getUser())
                )
            );
            embedBuilder.setDescription("This user has opted-out of reminders and cannot be reminded by other people.");
            embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.errorEmbedColorDecimal"));
            action.sendEmbedMessage(e -> {
                log.warn("Failed to send reminder creation failure message as embed.", e);
            }, event.getChannelId(), embedBuilder.build());
            return;
        }

        String remindAction = String.join(" ", args.getAction());
        long milli = TimeUtil.dhmsToMilli(args.getDays(), args.getHours(), args.getMinutes(), args.getSeconds());
        Instant now = Instant.now();

        Reminder reminder = Reminder.builder()
                                    .userTag(Formatter.formatTag(event.getAuthor()))
                                    .userId(args.getPerson().getUser().getId())
                                    .reminder(remindAction)
                                    .remindAt(now.plusMillis(milli))
                                    .build();

        log.info("Adding new reminder to reminder datastore.");

        Session.getSession().getReminderDataRepository().add(reminder);

        log.info("Successfully added new reminder to reminder datastore.");

        ReminderService.createInstance(reminder).start();

        log.debug(String.format("Spawned new reminder thread for %s.", Formatter.formatTag(event.getAuthor())));

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(
            String.format(
                "Okay. I will remind %s to...",
                Formatter.formatTag(args.getPerson().getUser())
            )
        );
        embedBuilder.setDescription(remindAction);
        embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.defaultEmbedColorDecimal"));

        action.sendEmbedMessage(e -> {
            log.warn("Failed to send reminder creation message as embed.", e);
        }, event.getChannelId(), embedBuilder.build());
    }

    private boolean isOptedOut(@NonNull User member) {
        Optional<GenericStringList> genericStringListOptional = Session.getSession()
                                                                       .getStringListDataRepository()
                                                                       .getStringList(
                                                                           GlobalConfiguration.REMINDER_OPT_OUT_LIST_KEY
                                                                       );

        if (!genericStringListOptional.isPresent()) {
            return false;
        }

        return genericStringListOptional.get().getList().contains(member.getId());
    }
}
