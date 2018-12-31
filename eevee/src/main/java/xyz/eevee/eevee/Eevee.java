package xyz.eevee.eevee;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import xyz.eevee.coffee.client.CoffeeRPCClient;
import xyz.eevee.eevee.bot.EeveeBot;
import xyz.eevee.eevee.configuration.CoffeeConfiguration;
import xyz.eevee.eevee.configuration.GenericJsonConfiguration;
import xyz.eevee.eevee.configuration.GlobalConfiguration;
import xyz.eevee.eevee.exc.InvalidConfigurationException;
import xyz.eevee.eevee.repository.GuildDataRepository;
import xyz.eevee.eevee.repository.HsReleaseAnnouncerDataRepository;
import xyz.eevee.eevee.repository.MangaDexReleaseAnnouncerDataRepository;
import xyz.eevee.eevee.repository.NyaaReleaseAnnouncerDataRepository;
import xyz.eevee.eevee.repository.ReminderDataRepository;
import xyz.eevee.eevee.repository.StringListDataRepository;
import xyz.eevee.eevee.repository.TweetAnnouncerDataRepository;
import xyz.eevee.eevee.session.BuildInfo;
import xyz.eevee.eevee.session.Session;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

@Log4j2
public class Eevee {
    public static void main(String[] args) {
        log.info("Starting new Eevee shard.");

        if (System.getenv("RUNTIME_ENV") != null && System.getenv("RUNTIME_ENV").equals("PROD")) {
            log.info("Shard is running in PROD.");
            log.info(String.format("Running on host: %s", System.getenv("NOMAD_HOST")));
            Session.getSession().setProd(true);
        }

        log.info(String.format(
            "Using Inside application token: %s", GlobalConfiguration.INSIDE_APP_TOKEN
        ));

        ObjectMapper objectMapper = new ObjectMapper();
        Session.getSession().setObjectMapper(objectMapper);

        CoffeeRPCClient coffeeClient = CoffeeRPCClient.builder()
                                                      .coffeeHost(GlobalConfiguration.COFFEE_HOST)
                                                      .coffeePort(GlobalConfiguration.COFFEE_PORT)
                                                      .insideAppToken(GlobalConfiguration.INSIDE_APP_TOKEN)
                                                      .build();

        CoffeeConfiguration coffeeConfiguration = CoffeeConfiguration.builder()
                                                                     .coffeeRpcClient(coffeeClient)
                                                                     .cacheTtlSeconds(
                                                                         GlobalConfiguration.COFFEE_CACHE_TTL_SECONDS
                                                                     )
                                                                     .build();

        GenericJsonConfiguration buildInfoJson =
            GenericJsonConfiguration.builder()
                                    .fileStream(
                                        Eevee.class.getClassLoader()
                                                   .getResourceAsStream(GlobalConfiguration.BUILD_INFO_PATH)
                                    )
                                    .build();

        BuildInfo buildInfo = BuildInfo.builder()
                                       .builtBy(buildInfoJson.readString(
                                           GlobalConfiguration.BUILD_BY_USER_NAME)
                                       )
                                       .builtByName(
                                           buildInfoJson.readString(GlobalConfiguration.BUILD_BY_NAME_NAME)
                                       )
                                       .builtById(
                                           buildInfoJson.readString(GlobalConfiguration.BUILD_BY_ID_NAME)
                                       )
                                       .ciCommitMessage(
                                           buildInfoJson.readString(
                                               GlobalConfiguration.BUILD_COMMIT_MESSAGE_NAME
                                           )
                                       )
                                       .ciCommitSha(
                                           buildInfoJson.readString(GlobalConfiguration.BUILD_SHA_NAME)
                                       )
                                       .ciJobId(
                                           buildInfoJson.readString(GlobalConfiguration.BUILD_JOB_ID_NAME)
                                       )
                                       .buildTime(
                                           buildInfoJson.readString(GlobalConfiguration.BUILD_TIMESTAMP_NAME)
                                       )
                                       .build();

        coffeeConfiguration.init();
        Session.getSession().setBuildInfo(buildInfo);
        Session.getSession().setConfiguration(coffeeConfiguration);
        Session.getSession().setGuildDataRepository(GuildDataRepository.getInstance());
        Session.getSession().setReminderDataRepository(ReminderDataRepository.getInstance());
        Session.getSession().setHsReleaseAnnouncerDataRepository(HsReleaseAnnouncerDataRepository.getInstance());
        Session.getSession().setTweetAnnouncerDataRepository(TweetAnnouncerDataRepository.getInstance());
        Session.getSession().setStringListDataRepository(StringListDataRepository.getInstance());
        Session.getSession().setMangaDexReleaseAnnouncerDataRepository((MangaDexReleaseAnnouncerDataRepository.getInstance()));
        Session.getSession().setNyaaReleaseAnnouncerDataRepository(NyaaReleaseAnnouncerDataRepository.getInstance());

        try {
            final String botToken = coffeeConfiguration.readString("eevee.botToken");

            log.info(String.format("Using bot token: %s.", botToken));

            JDA jda = new JDABuilder(AccountType.BOT).setToken(botToken)
                                                     .build();
            Session.getSession().setJdaClient(jda);
            EeveeBot bot = EeveeBot.builder().build();

            bot.start();
        } catch (
            LoginException |
            InvalidConfigurationException |
            NoSuchAlgorithmException |
            KeyManagementException |
            URISyntaxException |
            IOException |
            TimeoutException e
        ) {
            log.fatal("Failed to start Eevee shard.", e);
        }
    }
}
