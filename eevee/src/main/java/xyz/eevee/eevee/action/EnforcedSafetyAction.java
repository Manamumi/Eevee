// SANITY_IGNORE_ENFORCED_SAFETY

package xyz.eevee.eevee.action;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import xyz.eevee.eevee.exc.EeveeActionException;
import xyz.eevee.eevee.util.DeprecateWithPorygonUtil;

import java.util.function.Consumer;

/**
 * <p>
 * This class is used to enforce safety on actions that may potentially fail.
 * It provides exponentially delayed retries for free and enforces good practices
 * by refusing to run if no error handler is set.
 * </p>
 * <p>
 * This class has setters for changing properties after instantiation but in most
 * cases you will should not use them. It is better to use a unique instance for
 * each use case instead of using a single instance for all your needs.
 * </p>
 * <p>
 * The methods in this class are designed so that the user thinks about safety first.
 * The first parameter is always a failure handler followed by mandatory values to
 * execute an action. The callback is always last since people typically ignore error
 * handling and only care about callback values. This ensures that callbacks are always
 * the last thing on somebody's mind.
 * </p>
 */
@Log4j2
@Data
@Builder
@Accessors(chain = true)
public class EnforcedSafetyAction {
    /**
     * The initial delay before retrying an action if the first
     * attempt failed.
     */
    @Builder.Default
    private int retryMilli = 50;
    /**
     * The maximum number of times to retry a failed action.
     */
    @Builder.Default
    private int maxRetries = 3;
    /**
     * The amount of time to multiply by between subsequent retries.
     */
    @Builder.Default
    private double exponentialDelay = 2;
    /**
     * If this is set then all errors will passthrough to this
     * consumer.
     */
    private Consumer<Exception> exceptionHandler;

    /**
     * Attempt to invoke an action until either the maximum number
     * of retries is reached or it succeeds.
     */
    private void invoke(@NonNull Runnable runnable) {
        int currentDelayMilli = retryMilli;

        for (int currentTry = 0; currentTry < maxRetries; currentTry++) {
            try {
                runnable.run();
                return;
            } catch (Exception e) {
                log.warn(String.format(
                    "Failed to execute an action. Cause: %s",
                    e.getMessage()
                ), e);

                if (exceptionHandler != null) {
                    exceptionHandler.accept(e);
                }

                if (currentTry + 1 == maxRetries) {
                    throw new EeveeActionException("Failed to execute an action.", e);
                }

                try {
                    Thread.sleep(currentDelayMilli);
                } catch (InterruptedException ex) {
                    log.warn("Sleep was interrupted while waiting to retry an action.", ex);
                }

                currentDelayMilli *= exponentialDelay;
            }
        }
    }

    @SuppressWarnings("Duplicates")
    public void sendMessage(
        @NonNull Consumer<EeveeActionException> failureHandler,
        @NonNull String channelId,
        @NonNull String message,
        Consumer<Message> newMessageConsumer
    ) {
        try {
            invoke(() -> {
                TextChannel channel = DeprecateWithPorygonUtil.getTextChannelById(channelId);
                channel.sendMessage(message).queue((newMessage) -> {
                    if (newMessageConsumer != null) {
                        newMessageConsumer.accept(newMessage);
                    }
                }, (error) -> {
                    log.warn("Failed to send plaintext message.", error);
                    throw new EeveeActionException(
                        String.format(
                            "Could not send message due to unexpected exception. Error: %s",
                            error.getMessage()
                        ),
                        error
                    );
                });
            });
        } catch (EeveeActionException e) {
            failureHandler.accept(e);
        }
    }

    public void sendMessage(
        @NonNull Consumer<EeveeActionException> failureHandler,
        @NonNull String channelId,
        @NonNull String message
    ) {
        sendMessage(failureHandler, channelId, message, null);
    }

    @SuppressWarnings("Duplicates")
    public void sendEmbedMessage(
        @NonNull Consumer<EeveeActionException> failureHandler,
        @NonNull String channelId,
        @NonNull MessageEmbed message,
        Consumer<Message> newMessageConsumer
    ) {
        try {
            invoke(() -> {
                TextChannel channel = DeprecateWithPorygonUtil.getTextChannelById(channelId);
                channel.sendMessage(message).queue((newMessage) -> {
                    if (newMessageConsumer != null) {
                        newMessageConsumer.accept(newMessage);
                    }
                }, (error) -> {
                    log.warn("Failed to send message embed.", error);
                    throw new EeveeActionException(
                        String.format(
                            "Could not send message due to unexpected exception. Error: %s",
                            error.getMessage()
                        ),
                        error
                    );
                });
            });
        } catch (EeveeActionException e) {
            failureHandler.accept(e);
        }
    }

    public void sendEmbedMessage(
        @NonNull Consumer<EeveeActionException> failureHandler,
        @NonNull String channelId,
        @NonNull MessageEmbed message
    ) {
        sendEmbedMessage(failureHandler, channelId, message, null);
    }

    public void deleteMessage(
        @NonNull Consumer<EeveeActionException> failureHandler,
        @NonNull String channelId,
        @NonNull String messageId,
        Runnable callback
    ) {
        try {
            invoke(() -> {
                DeprecateWithPorygonUtil.getTextChannelById(channelId)
                                        .getMessageById(messageId)
                                        .queue(message -> {
                                            message.delete().queue((success) -> {
                                                if (callback != null) {
                                                    callback.run();
                                                }
                                            }, (error) -> {
                                                log.warn("Failed to delete message.", error);
                                                throw new EeveeActionException(
                                                    String.format(
                                                        "Could not delete message due to unexpected exception. Error: %s",
                                                        error.getMessage()
                                                    ),
                                                    error
                                                );
                                            });
                                        });
            });
        } catch (EeveeActionException e) {
            failureHandler.accept(e);
        }
    }

    public void deleteMessage(
        @NonNull Consumer<EeveeActionException> failureHandler,
        @NonNull String channelId,
        @NonNull String messageId
    ) {
        deleteMessage(failureHandler, channelId, messageId, null);
    }

    public void editMessage(
        @NonNull Consumer<EeveeActionException> failureHandler,
        @NonNull Message message,
        @NonNull MessageEmbed newEmbed,
        Consumer<Message> editedMessageConsumer
    ) {
        try {
            invoke(() -> {
                message.editMessage(newEmbed).queue((newMessage) -> {
                    if (editedMessageConsumer != null) {
                        editedMessageConsumer.accept(newMessage);
                    }
                }, (error) -> {
                    log.warn("Failed to edit message.", error);
                    throw new EeveeActionException(
                        String.format(
                            "Could not edit message due to unexpected exception. Error: %s",
                            error.getMessage()
                        ),
                        error
                    );
                });
            });
        } catch (EeveeActionException e) {
            failureHandler.accept(e);
        }
    }

    public void editMessage(
        @NonNull Consumer<EeveeActionException> failureHandler,
        @NonNull Message message,
        @NonNull MessageEmbed newEmbed
    ) {
        editMessage(failureHandler, message, newEmbed, null);
    }

    public void openPrivateChannel(
        @NonNull Consumer<EeveeActionException> failureHandler,
        @NonNull String userId,
        Consumer<PrivateChannel> privateChannelConsumer
    ) {
        try {
            invoke(() -> {
                DeprecateWithPorygonUtil.getUserById(userId).openPrivateChannel().queue(privateChannelConsumer, error -> {
                    log.warn("Failed to open private channel for user.", error);
                    throw new EeveeActionException(
                        String.format(
                            "Could not open private channel due to unexpected exception. Error: %s",
                            error.getMessage()
                        ),
                        error
                    );
                });
            });
        } catch (EeveeActionException e) {
            failureHandler.accept(e);
        }
    }

    public void openPrivateChannel(
        @NonNull Consumer<EeveeActionException> failureHandler,
        @NonNull String userId
    ) {
        openPrivateChannel(failureHandler, userId, null);
    }
}
