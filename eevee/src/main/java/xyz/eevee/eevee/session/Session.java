package xyz.eevee.eevee.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.dv8tion.jda.core.JDA;
import xyz.eevee.eevee.bot.CommandMapper;
import xyz.eevee.eevee.configuration.Configuration;
import xyz.eevee.eevee.repository.GuildDataRepository;
import xyz.eevee.eevee.repository.HsReleaseAnnouncerDataRepository;
import xyz.eevee.eevee.repository.MangaDexReleaseAnnouncerDataRepository;
import xyz.eevee.eevee.repository.NyaaReleaseAnnouncerDataRepository;
import xyz.eevee.eevee.repository.ReminderDataRepository;
import xyz.eevee.eevee.repository.StringListDataRepository;
import xyz.eevee.eevee.repository.TweetAnnouncerDataRepository;

@NoArgsConstructor
@Data
public class Session {
    @NonNull
    private Configuration configuration;
    @NonNull
    private JDA jdaClient;
    @NonNull
    private ReminderDataRepository reminderDataRepository;
    @NonNull
    private HsReleaseAnnouncerDataRepository hsReleaseAnnouncerDataRepository;
    @NonNull
    private TweetAnnouncerDataRepository tweetAnnouncerDataRepository;
    @NonNull
    private ObjectMapper objectMapper;
    @NonNull
    private BuildInfo buildInfo;
    @NonNull
    private StringListDataRepository stringListDataRepository;
    @NonNull
    private CommandMapper commandMapper;
    @NonNull
    private GuildDataRepository guildDataRepository;
    @NonNull
    private MangaDexReleaseAnnouncerDataRepository mangaDexReleaseAnnouncerDataRepository;
    @NonNull
    private NyaaReleaseAnnouncerDataRepository nyaaReleaseAnnouncerDataRepository;

    private boolean isProd;

    private static Session session;

    public static Session getSession() {
        if (session == null) {
            session = new Session();
        }

        return session;
    }
}
