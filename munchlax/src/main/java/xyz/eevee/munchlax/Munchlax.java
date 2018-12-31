package xyz.eevee.munchlax;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import xyz.eevee.coffee.client.CoffeeRPCClient;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

@Log4j2
public class Munchlax {
    public static void main(String[] args) {
        log.info("Starting new Munchlax shard.");

        final String INSIDE_APP_TOKEN = System.getenv("INSIDE_APP_TOKEN");
        final String COFFEE_HOST = "127.0.0.1";
        final int COFFEE_PORT = 7733;

        log.info(String.format(
            "Using Coffee server @ %s:%s", COFFEE_HOST, COFFEE_PORT
        ));

        log.info(String.format(
            "Using Inside application token: %s", INSIDE_APP_TOKEN
        ));

        CoffeeRPCClient coffeeClient = CoffeeRPCClient.builder()
                                                      .coffeeHost(COFFEE_HOST)
                                                      .coffeePort(COFFEE_PORT)
                                                      .insideAppToken(INSIDE_APP_TOKEN)
                                                      .build();

        try {
            final String botToken = coffeeClient.getString("eevee.botToken");
            log.info(String.format("Using bot token: %s.", botToken));
            final MunchlaxAdapter munchlaxAdapter = MunchlaxAdapter.builder()
                                                                   .coffeeRPCClient(coffeeClient)
                                                                   .build();

            munchlaxAdapter.setupIngestQueue();
            new JDABuilder(AccountType.BOT).setToken(botToken)
                                           .addEventListener(munchlaxAdapter)
                                           .setGame(Game.watching("ev help"))
                                           .build();
        } catch (
            LoginException |
            IOException |
            NoSuchAlgorithmException |
            KeyManagementException |
            URISyntaxException |
            TimeoutException e
        ) {
            log.fatal("Failed to start Munchlax shard.", e);
        }
    }
}
