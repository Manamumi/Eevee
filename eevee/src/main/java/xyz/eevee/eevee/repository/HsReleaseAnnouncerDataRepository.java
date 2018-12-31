package xyz.eevee.eevee.repository;

import com.google.common.collect.ImmutableList;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Collation;
import com.mongodb.client.model.CollationStrength;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import xyz.eevee.eevee.provider.MongoClientProvider;
import xyz.eevee.eevee.repository.model.HsReleaseAnnouncer;
import xyz.eevee.eevee.session.Session;

import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

@Log4j2
public class HsReleaseAnnouncerDataRepository extends DataRepository {
    private static HsReleaseAnnouncerDataRepository dataRepository;
    private final MongoCollection<HsReleaseAnnouncer> mongoCollection;

    private HsReleaseAnnouncerDataRepository() {
        super();
        MongoClient mongoClient = MongoClientProvider.getInstance();
        String databaseName = Session.getSession().getConfiguration().readString("eevee.mongoDatabaseName");
        String collectionName = Session.getSession()
                                       .getConfiguration()
                                       .readString("eevee.mongoHSReleaseAnnouncerCollectionName");
        mongoCollection = mongoClient.getDatabase(databaseName)
                                     .getCollection(collectionName, HsReleaseAnnouncer.class);
    }

    public static HsReleaseAnnouncerDataRepository getInstance() {
        if (dataRepository == null) {
            dataRepository = new HsReleaseAnnouncerDataRepository();
        }

        return dataRepository;
    }

    public static HsReleaseAnnouncerDataRepository reload() {
        return (dataRepository = new HsReleaseAnnouncerDataRepository());
    }

    public List<HsReleaseAnnouncer> getAnnouncers() {
        return ImmutableList.copyOf(mongoCollection.find());
    }

    public Optional<HsReleaseAnnouncer> getAnnouncer(
        @NonNull String anime,
        @NonNull String quality,
        @NonNull String channelId
    ) {
        return Optional.ofNullable(mongoCollection.find(
            and(
                eq("channelId", channelId),
                eq("anime", anime),
                eq("quality", quality)
            )
        ).collation(
            Collation.builder()
                     .locale("en")
                     .collationStrength(CollationStrength.PRIMARY)
                     .build()
        ).first());
    }

    public void add(@NonNull HsReleaseAnnouncer announcer) {
        mongoCollection.insertOne(announcer);
    }

    public void update(@NonNull HsReleaseAnnouncer announcer) {
        mongoCollection.replaceOne(eq("announcerId", announcer.getAnnouncerId()), announcer);
    }

    public void remove(@NonNull HsReleaseAnnouncer announcer) {
        mongoCollection.deleteOne(eq("announcerId", announcer.getAnnouncerId()));
    }
}
