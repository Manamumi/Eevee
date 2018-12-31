package xyz.eevee.munchlax;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import xyz.eevee.coffee.client.CoffeeRPCClient;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

@Log4j2
@Builder
public class MunchlaxAdapter extends ListenerAdapter {
    @NonNull
    private CoffeeRPCClient coffeeRPCClient;
    private Channel ingestChannel;
    private String newMessageIngestQueueName;
    private String guildMemberJoinIngestQueueName;

    void setupIngestQueue() throws IOException, TimeoutException, NoSuchAlgorithmException, KeyManagementException, URISyntaxException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(coffeeRPCClient.getString("munchlax.ingestQueueUri"));
        Connection connection = factory.newConnection();
        ingestChannel = connection.createChannel();
        newMessageIngestQueueName = coffeeRPCClient.getString("munchlax.newMessageIngestQueueName");
        guildMemberJoinIngestQueueName = coffeeRPCClient.getString("munchlax.guildMemberJoinIngestQueueName");

        ingestChannel.queueDeclare(
            newMessageIngestQueueName,
            true,
            false,
            false,
            null
        );

        ingestChannel.queueDeclare(
            guildMemberJoinIngestQueueName,
            true,
            false,
            false,
            null
        );

        log.info("Successfully connected to ingest channel and declared queues.");
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        NewMessageEvent newMessageEvent = Marshal.marshalMessageEvent(event);

        try {
            ingestChannel.basicPublish(
                "",
                newMessageIngestQueueName,
                MessageProperties.BASIC,
                newMessageEvent.toByteArray()
            );
        } catch (IOException e) {
            log.warn("Failed to publish new messsage event to ingest queue.", e);
        }
    }
}
