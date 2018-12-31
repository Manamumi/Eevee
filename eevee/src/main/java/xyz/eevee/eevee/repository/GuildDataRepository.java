package xyz.eevee.eevee.repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import xyz.eevee.eevee.provider.MongoClientProvider;
import xyz.eevee.eevee.repository.model.Guild;
import xyz.eevee.eevee.session.Session;

import java.util.Optional;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

@Log4j2
public class GuildDataRepository extends DataRepository {
    private static GuildDataRepository dataRepository;
    private final MongoCollection<Guild> mongoCollection;

    private GuildDataRepository() {
        super();
        MongoClient mongoClient = MongoClientProvider.getInstance();
        String collectionName = Session.getSession().getConfiguration().readString("eevee.mongoGuildCollectionName");
        String databaseName = Session.getSession().getConfiguration().readString("eevee.mongoDatabaseName");
        mongoCollection = mongoClient.getDatabase(databaseName).getCollection(collectionName, Guild.class);
    }

    public static GuildDataRepository getInstance() {
        if (dataRepository == null) {
            dataRepository = new GuildDataRepository();
        }

        return dataRepository;
    }

    public static GuildDataRepository reload() {
        return (dataRepository = new GuildDataRepository());
    }

    public void add(@NonNull Guild guild) {
        mongoCollection.insertOne(guild);
    }

    public Optional<Guild> get(@NonNull String serverId) {
        return Optional.ofNullable(mongoCollection.find(
            and(
                eq("serverId", serverId)
            )
        ).first());
    }

    public void update(@NonNull Guild guild) {
        mongoCollection.replaceOne(eq("serverId", guild.getServerId()), guild);
    }

    public void remove(@NonNull Guild guild) {
        mongoCollection.deleteOne(
            and(
                eq("serverId", guild.getServerId())
            )
        );
    }
}
