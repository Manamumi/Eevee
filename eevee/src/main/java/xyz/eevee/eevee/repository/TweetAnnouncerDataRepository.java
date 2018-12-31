package xyz.eevee.eevee.repository;

import com.google.common.collect.ImmutableList;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Collation;
import com.mongodb.client.model.CollationStrength;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import xyz.eevee.eevee.provider.MongoClientProvider;
import xyz.eevee.eevee.repository.model.TweetAnnouncer;
import xyz.eevee.eevee.session.Session;

import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

@Log4j2
public class TweetAnnouncerDataRepository extends DataRepository {
    private static TweetAnnouncerDataRepository dataRepository;
    private final MongoCollection<TweetAnnouncer> mongoCollection;

    private TweetAnnouncerDataRepository() {
        super();
        MongoClient mongoClient = MongoClientProvider.getInstance();
        String collectionName = Session.getSession()
                                       .getConfiguration()
                                       .readString("eevee.mongoTweetAnnouncerCollectionName");
        String databaseName = Session.getSession().getConfiguration().readString("eevee.mongoDatabaseName");
        mongoCollection = mongoClient.getDatabase(databaseName)
                                     .getCollection(collectionName, TweetAnnouncer.class);
    }

    public static TweetAnnouncerDataRepository getInstance() {
        if (dataRepository == null) {
            dataRepository = new TweetAnnouncerDataRepository();
        }

        return dataRepository;
    }

    public static TweetAnnouncerDataRepository reload() {
        return (dataRepository = new TweetAnnouncerDataRepository());
    }

    public List<TweetAnnouncer> getAnnouncers() {
        return ImmutableList.copyOf(mongoCollection.find());
    }

    public Optional<TweetAnnouncer> getAnnouncer(@NonNull String user, @NonNull String channelId) {
        return Optional.ofNullable(mongoCollection.find(
            and(
                eq("channelId", channelId),
                eq("user", user)
            )
        ).collation(
            Collation.builder()
                     .locale("en")
                     .collationStrength(CollationStrength.PRIMARY)
                     .build()
        ).first());
    }

    public void add(@NonNull TweetAnnouncer announcer) {
        mongoCollection.insertOne(announcer);
    }

    public void update(@NonNull TweetAnnouncer announcer) {
        mongoCollection.replaceOne(eq("announcerId", announcer.getAnnouncerId()), announcer);
    }

    public void remove(@NonNull TweetAnnouncer announcer) {
        mongoCollection.deleteOne(eq("announcerId", announcer.getAnnouncerId()));
    }
}
