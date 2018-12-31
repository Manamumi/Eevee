package xyz.eevee.eevee.repository;

import com.google.common.collect.ImmutableList;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Collation;
import com.mongodb.client.model.CollationStrength;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import xyz.eevee.eevee.provider.MongoClientProvider;
import xyz.eevee.eevee.repository.model.NyaaReleaseAnnouncer;
import xyz.eevee.eevee.session.Session;

import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

@Log4j2
public class NyaaReleaseAnnouncerDataRepository extends DataRepository {
    private static NyaaReleaseAnnouncerDataRepository dataRepository;
    private final MongoCollection<NyaaReleaseAnnouncer> mongoCollection;

    private NyaaReleaseAnnouncerDataRepository() {
        super();
        MongoClient mongoClient = MongoClientProvider.getInstance();
        String databaseName = Session.getSession().getConfiguration().readString("eevee.mongoDatabaseName");
        String collectionName = Session.getSession()
                                       .getConfiguration()
                                       .readString("eevee.mongoNyaaReleaseAnnouncerCollectionName");
        mongoCollection = mongoClient.getDatabase(databaseName)
                                     .getCollection(collectionName, NyaaReleaseAnnouncer.class);
    }

    public static NyaaReleaseAnnouncerDataRepository getInstance() {
        if (dataRepository == null) {
            dataRepository = new NyaaReleaseAnnouncerDataRepository();
        }

        return dataRepository;
    }

    public static NyaaReleaseAnnouncerDataRepository reload() {
        return (dataRepository = new NyaaReleaseAnnouncerDataRepository());
    }

    public List<NyaaReleaseAnnouncer> getAnnouncers() {
        return ImmutableList.copyOf(mongoCollection.find());
    }

    public Optional<NyaaReleaseAnnouncer> getAnnouncer(
        @NonNull String subber,
        @NonNull String anime,
        @NonNull String quality,
        @NonNull String channelId
    ) {
        return Optional.ofNullable(mongoCollection.find(
            and(
                eq("channelId", channelId),
                eq("anime", anime),
                eq("subber", subber),
                eq("quality", quality)
            )
        ).collation(
            Collation.builder()
                     .locale("en")
                     .collationStrength(CollationStrength.PRIMARY)
                     .build()
        ).first());
    }

    public void add(@NonNull NyaaReleaseAnnouncer announcer) {
        mongoCollection.insertOne(announcer);
    }

    public void update(@NonNull NyaaReleaseAnnouncer announcer) {
        mongoCollection.replaceOne(eq("announcerId", announcer.getAnnouncerId()), announcer);
    }

    public void remove(@NonNull NyaaReleaseAnnouncer announcer) {
        mongoCollection.deleteOne(eq("announcerId", announcer.getAnnouncerId()));
    }
}
