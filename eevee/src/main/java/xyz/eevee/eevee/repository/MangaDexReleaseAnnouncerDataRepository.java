package xyz.eevee.eevee.repository;

import com.google.common.collect.ImmutableList;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Collation;
import com.mongodb.client.model.CollationStrength;
import lombok.NonNull;
import xyz.eevee.eevee.provider.MongoClientProvider;
import xyz.eevee.eevee.repository.model.MangaDexReleaseAnnouncer;
import xyz.eevee.eevee.session.Session;

import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class MangaDexReleaseAnnouncerDataRepository extends DataRepository {
    private static MangaDexReleaseAnnouncerDataRepository dataRepository;
    private final MongoCollection<MangaDexReleaseAnnouncer> mongoCollection;

    private MangaDexReleaseAnnouncerDataRepository() {
        super();
        MongoClient mongoClient = MongoClientProvider.getInstance();
        String databaseName = Session.getSession().getConfiguration().readString("eevee.mongoDatabaseName");
        String collectionName = Session.getSession()
                                       .getConfiguration()
                                       .readString("eevee.mongoMangaReleaseAnnouncerCollectionName");
        mongoCollection = mongoClient.getDatabase(databaseName)
                                     .getCollection(collectionName, MangaDexReleaseAnnouncer.class);
    }

    public static MangaDexReleaseAnnouncerDataRepository getInstance() {
        if (dataRepository == null) {
            dataRepository = new MangaDexReleaseAnnouncerDataRepository();
        }

        return dataRepository;
    }

    public static MangaDexReleaseAnnouncerDataRepository reload() {
        return (dataRepository = new MangaDexReleaseAnnouncerDataRepository());
    }

    public List<MangaDexReleaseAnnouncer> getAnnouncers() {
        return ImmutableList.copyOf(mongoCollection.find());
    }

    public Optional<MangaDexReleaseAnnouncer> getAnnouncer(
        @NonNull String title,
        @NonNull String scanlationGroup,
        @NonNull String channelId
    ) {
        return Optional.ofNullable(mongoCollection.find(
            and(
                eq("channelId", channelId),
                eq("scanlator", scanlationGroup),
                eq("title", title)
            )
        ).collation(
            Collation.builder()
                     .locale("en")
                     .collationStrength(CollationStrength.PRIMARY)
                     .build()
        ).first());
    }

    public void add(@NonNull MangaDexReleaseAnnouncer announcer) {
        mongoCollection.insertOne(announcer);
    }

    public void update(@NonNull MangaDexReleaseAnnouncer announcer) {
        mongoCollection.replaceOne(eq("announcerId", announcer.getAnnouncerId()), announcer);
    }

    public void remove(@NonNull MangaDexReleaseAnnouncer announcer) {
        mongoCollection.deleteOne(eq("announcerId", announcer.getAnnouncerId()));
    }
}