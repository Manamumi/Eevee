package xyz.eevee.eevee.bot;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import xyz.eevee.eevee.action.EnforcedSafetyAction;
import xyz.eevee.eevee.alerts.GuildJoinAlert;
import xyz.eevee.eevee.bot.command.Command;
import xyz.eevee.eevee.bot.command.dev.DevCommandGroup;
import xyz.eevee.eevee.bot.command.fun.FunCommandGroup;
import xyz.eevee.eevee.bot.command.subscription.SubscriptionCommandGroup;
import xyz.eevee.eevee.bot.command.util.UtilityCommandGroup;
import xyz.eevee.eevee.bot.command.util.welcome.WelcomeCommand;
import xyz.eevee.eevee.parser.Tokenizer;
import xyz.eevee.eevee.service.BtDownloadService;
import xyz.eevee.eevee.service.HsReleaseAnnouncerService;
import xyz.eevee.eevee.service.MangaDexReleaseAnnouncerService;
import xyz.eevee.eevee.service.NyaaReleaseAnnouncerService;
import xyz.eevee.eevee.service.ReminderService;
import xyz.eevee.eevee.service.TemporalTweetAnnouncerService;
import xyz.eevee.eevee.session.Session;
import xyz.eevee.munchlax.GuildMemberJoinEvent;
import xyz.eevee.munchlax.NewMessageEvent;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

@Log4j2
@Builder
public class EeveeBot {
    private CommandMapper commandMapper;
    private ExecutorService executor;

    public void registerCommands() {
        if (commandMapper != null) {
            log.info("Bot commands already registered. Skipping.");
            return;
        }

        log.info("Registering commands");

        commandMapper = new CommandMapper();

        commandMapper.addModule(new DevCommandGroup());
        commandMapper.addModule(new FunCommandGroup());
        commandMapper.addModule(new SubscriptionCommandGroup());
        commandMapper.addModule(new UtilityCommandGroup());

        Session.getSession().setCommandMapper(commandMapper);

        log.info(String.format("Registered %s commands.", commandMapper.getBotCommands().size()));

        executor = Executors.newFixedThreadPool(
            Session.getSession()
                   .getConfiguration()
                   .readInt("eevee.numHandlerThreads")
        );

        Session.getSession()
               .getReminderDataRepository()
               .getReminders()
               .stream()
               .map(ReminderService::createInstance)
               .forEach(ReminderService::start);
        HsReleaseAnnouncerService.getInstance().start();
        TemporalTweetAnnouncerService.getInstance().start();
        BtDownloadService.getInstance().start();
        MangaDexReleaseAnnouncerService.getInstance().start();
        NyaaReleaseAnnouncerService.getInstance().start();
    }

    public void start() throws NoSuchAlgorithmException, KeyManagementException, URISyntaxException, IOException, TimeoutException {
        EeveeBot that = this;

        registerCommands();

        String ingestQueueUri = Session.getSession()
                                       .getConfiguration()
                                       .readString("munchlax.ingestQueueUri");
        String newMessageIngestQueueName = Session.getSession()
                                                  .getConfiguration()
                                                  .readString("munchlax.newMessageIngestQueueName");

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setUri(ingestQueueUri);
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(
            newMessageIngestQueueName,
            true,
            false,
            false,
            null
        );

        Consumer consumer = new DefaultConsumer(channel) {
            public void handleDelivery(
                String consumerTag,
                Envelope envelope,
                AMQP.BasicProperties properties,
                byte[] body
            ) throws InvalidProtocolBufferException {
                NewMessageEvent event = NewMessageEvent.parseFrom(body);
                that.onMessageReceived(event);
            }
        };

        channel.basicConsume(newMessageIngestQueueName, true, consumer);
    }

    public void onGuildJoin(GuildJoinEvent event) {
        GuildJoinAlert.announce(event);
    }

    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        WelcomeCommand.welcomeListener(event);
    }

    public void onMessageReceived(@NonNull final NewMessageEvent event) {
        String messageText = event.getContent().trim();
        String botPrefix = Session.getSession().getConfiguration().readString("eevee.botPrefix");

        if (!messageText.startsWith(botPrefix)) {
            return;
        }

        System.out.println(messageText);

        log.debug(String.format("Received potential command: %s.", messageText));

        final String commandText = messageText.substring(botPrefix.length());
        final Tokenizer tokenizer = new Tokenizer(commandText);

        Optional<Command> commandOptional = commandMapper.get(tokenizer, event);

        if (!commandOptional.isPresent()) {
            return;
        }

        Command command = commandOptional.get();

        executor.submit(() -> {
            try {
                command.invoke(event, command.getArguments().parse(tokenizer, event));
            } catch (PermissionException e) {
                log.warn(
                    String.format(
                        "Failed to execute command due to insufficient permission. Command:%s%n",
                        command.getShortLabel()
                    ),
                    e
                );

                EnforcedSafetyAction.builder()
                                    .build()
                                    .sendMessage(ex -> {
                                            log.warn("Failed to send missing permission error as plaintext.", ex);
                                        }, event.getChannelId(),
                                        "Looks like I'm missing a permission. Make sure I have " +
                                            "all the permissions I asked for when you added me!");
            } catch (RuntimeException e) {
                log.error(
                    String.format(
                        "Failed to execute command due to unhandled runtime exception. Command:%s%n",
                        command.getShortLabel()
                    ),
                    e
                );
            }
        });
    }
}
