package xyz.eevee.eevee.repository;

import com.google.common.collect.ImmutableList;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import xyz.eevee.eevee.provider.MongoClientProvider;
import xyz.eevee.eevee.repository.model.GenericStringList;
import xyz.eevee.eevee.session.Session;

import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

@Log4j2
public class StringListDataRepository extends DataRepository {
    private static StringListDataRepository dataRepository;
    private final MongoCollection<GenericStringList> mongoCollection;

    private StringListDataRepository() {
        super();
        MongoClient mongoClient = MongoClientProvider.getInstance();
        String collectionName = Session.getSession()
                                       .getConfiguration()
                                       .readString("eevee.mongoStringListCollectionName");
        String databaseName = Session.getSession().getConfiguration().readString("eevee.mongoDatabaseName");
        mongoCollection = mongoClient.getDatabase(databaseName)
                                     .getCollection(collectionName, GenericStringList.class);
    }

    public static StringListDataRepository getInstance() {
        if (dataRepository == null) {
            dataRepository = new StringListDataRepository();
        }

        return dataRepository;
    }

    public static StringListDataRepository reload() {
        return (dataRepository = new StringListDataRepository());
    }

    public Optional<GenericStringList> getStringList(@NonNull String key) {
        return Optional.ofNullable(mongoCollection.find(
            and(
                eq("key", key)
            )
        ).first());
    }

    public void add(@NonNull String key, @NonNull String value) {
        Optional<GenericStringList> genericStringListOptional = getStringList(key);

        if (!genericStringListOptional.isPresent()) {
            mongoCollection.insertOne(
                GenericStringList.builder()
                                 .key(key)
                                 .list(ImmutableList.of(value))
                                 .build()
            );
        } else {
            GenericStringList genericStringList = genericStringListOptional.get();
            List<String> list = genericStringList.getList();
            list.add(value);
            update(genericStringList);
        }
    }

    public void update(@NonNull GenericStringList genericStringList) {
        mongoCollection.replaceOne(eq("key", genericStringList.getKey()), genericStringList);
    }

    public void remove(@NonNull GenericStringList genericStringList) {
        mongoCollection.deleteOne(eq("key", genericStringList.getKey()));
    }
}
