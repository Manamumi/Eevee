package xyz.eevee.eevee.bot.command.util.stats;

import com.google.common.collect.ImmutableList;
import common.util.TimeUtil;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.JDA;
import xyz.eevee.eevee.action.EnforcedSafetyAction;
import xyz.eevee.eevee.bot.command.Command;
import xyz.eevee.eevee.bot.command.CommandArguments;
import xyz.eevee.eevee.bot.command.CommandGroup;
import xyz.eevee.eevee.configuration.GlobalConfiguration;
import xyz.eevee.eevee.parser.arguments.Arguments;
import xyz.eevee.eevee.parser.arguments.LiteralArgument;
import xyz.eevee.eevee.session.Session;
import xyz.eevee.eevee.util.StatsUtil;
import xyz.eevee.munchlax.NewMessageEvent;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;

@Log4j2
public class StatsCommand extends Command {
    private Instant startTime;

    public StatsCommand(CommandGroup commandGroup) {
        super(commandGroup);
    }

    @Override
    public void bootstrap() {
        startTime = Instant.now();
    }

    @Override
    public String getShortLabel() {
        return "stats";
    }

    @Override
    public String getLabel() {
        return "Bot Stats";
    }

    @Override
    public String getDescription() {
        return "Displays bot stats.";
    }

    @Override
    public String getExample() {
        return "ev stats";
    }

    @Override
    public Arguments<StatsCommandArguments> getArguments() {
        return new Arguments<>(ImmutableList.of(
            new LiteralArgument("stats")
        ), StatsCommandArguments.class);
    }

    @Override
    public void invoke(@NonNull NewMessageEvent event, @NonNull CommandArguments arguments) {
        final int NANO_PER_MILLI = 1000 * 1000;
        Instant now = Instant.now();
        Duration uptime = Duration.between(startTime, now);
        String uptimeString = TimeUtil.durationToDdHhMmSs(uptime);
        JDA jdaClient = Session.getSession().getJdaClient();
        int numberOfServers = jdaClient.getGuilds().size();
        int numberOfMembers = jdaClient.getGuilds()
                                       .stream()
                                       .map(
                                           g -> g.getMembers().size()
                                       )
                                       .reduce(0, (a, b) -> a + b);

        int coffeePing = -1;

        try {
            Instant startTimeCoffee = Instant.now();
            Session.getSession().getConfiguration().readString(GlobalConfiguration.COFFEE_PING_KEY);
            Instant endTimeCoffee = Instant.now();
            coffeePing = Duration.between(startTimeCoffee, endTimeCoffee).getNano() / NANO_PER_MILLI;
        } catch (Exception e) {
            log.error("Unknown error occurred while pinging Coffee.", e);
        }

        final int finalCoffeePing = coffeePing;

        int insidePing = -1;

        try {
            URL url = new URL(
                Session.getSession()
                       .getConfiguration()
                       .readString("eevee.insideUrl")
            );
            Instant startTimeInside = Instant.now();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.getResponseCode();
            Instant endTimeInside = Instant.now();
            insidePing = Duration.between(startTimeInside, endTimeInside).getNano() / NANO_PER_MILLI;
        } catch (MalformedURLException e) {
            throw new RuntimeException("Somebody messed up the Coffee configuration for eevee.insideUrl.");
        } catch (IOException e) {
            log.warn("Unable to open connection to Inside to check ping.", e);
        }

        final int finalInsidePing = insidePing;
        Instant queueTime = Instant.now();

        EnforcedSafetyAction.builder()
                            .build()
                            .sendMessage(e -> {
                                log.warn("Failed to send stats as embed.", e);
                            }, event.getChannelId(), ".", m -> {
                                int apiPing = Duration.between(
                                    queueTime, m.getCreationTime()
                                ).getNano() / NANO_PER_MILLI;

                                EnforcedSafetyAction.builder()
                                                    .build()
                                                    .editMessage(error -> {
                                                        log.warn(
                                                            "Failed to update previous placeholder text.",
                                                            error
                                                        );
                                                    }, m, StatsUtil.createStatsEmbed(
                                                        jdaClient.getSelfUser().getEffectiveAvatarUrl(),
                                                        uptimeString,
                                                        numberOfServers,
                                                        numberOfMembers,
                                                        (int) jdaClient.getPing(),
                                                        apiPing / 2,
                                                        finalCoffeePing,
                                                        finalInsidePing,
                                                        event.getMember()
                                                    ));
                            });
    }
}
