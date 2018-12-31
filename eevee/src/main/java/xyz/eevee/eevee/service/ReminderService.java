package xyz.eevee.eevee.service;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.PrivateChannel;
import xyz.eevee.eevee.action.EnforcedSafetyAction;
import xyz.eevee.eevee.repository.model.Reminder;
import xyz.eevee.eevee.session.Session;
import xyz.eevee.eevee.util.Formatter;
import xyz.eevee.munchlax.User;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Log4j2
public final class ReminderService implements Service {
    private final Reminder reminder;

    private ReminderService(@NonNull Reminder reminder) {
        this.reminder = reminder;
    }

    public static ReminderService createInstance(@NonNull Reminder reminder) {
        return new ReminderService(reminder);
    }

    @Override
    public void start() {
        Instant now = Instant.now();
        Instant remindAt = reminder.getRemindAt();
        Duration difference = Duration.between(now, remindAt);
        long milli = difference.getSeconds() < 0 ? 0 : difference.getSeconds() * 1000;

        Thread thread = new Thread("ReminderServiceThread") {
            public void run() {
                log.debug(String.format("Sleeping reminder thread for %s ms.", milli));

                try {
                    TimeUnit.MILLISECONDS.sleep(milli);
                } catch (InterruptedException e) {
                    log.warn("Failed to sleep reminder thread.", e);
                }

                User user = User.newBuilder()
                                .setId(reminder.getUserId())
                                .build();

                EnforcedSafetyAction.builder()
                                    .build()
                                    .openPrivateChannel(e -> {
                                        log.error(
                                            String.format(
                                                "Failed to open private channel to remind user %s.",
                                                Formatter.formatTag(user)
                                            )
                                        );
                                    }, user.getId(), channel -> {
                                        issueReminder(
                                            reminder.getReminder(),
                                            channel,
                                            () -> {
                                                Session.getSession().getReminderDataRepository().remove(reminder);
                                                log.debug(String.format(
                                                    "Issued reminder to %s. Reminder thread will die",
                                                    reminder.getUserTag()
                                                ));
                                            }
                                        );
                                    });
            }
        };

        thread.start();
    }

    private void issueReminder(
        @NonNull String reminder,
        @NonNull PrivateChannel channel,
        @NonNull Runnable cb
    ) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Reminder");
        embedBuilder.setDescription(reminder);
        embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.defaultEmbedColorDecimal"));

        EnforcedSafetyAction.builder()
                            .build()
                            .sendEmbedMessage(e -> {
                                log.error("Failed to send reminder message as embed.", e);
                            }, channel.getId(), embedBuilder.build(), m -> {
                                cb.run();
                            });
    }
}
