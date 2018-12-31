package xyz.eevee.eevee.provider;

import com.google.common.collect.ImmutableList;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.ClusterSettings;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import xyz.eevee.eevee.session.Session;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoClientProvider {
    private static MongoClient mongoClient;

    /**
     * Returns a Mongo client. Subsequent calls to this method will yield the same client object.
     *
     * @return A Mongo client.
     */
    public static MongoClient getInstance() {
        if (mongoClient != null) {
            return mongoClient;
        }

        CodecRegistry pojoCodecRegistry = fromRegistries(
                getDefaultCodecRegistry(),
                fromProviders(
                        PojoCodecProvider.builder().automatic(true).build()
                )
        );

        String serverAddress = Session.getSession()
                                      .getConfiguration().readString("eevee.mongoHost");

        ClusterSettings clusterSettings = ClusterSettings.builder()
                                                         .hosts(ImmutableList.of(new ServerAddress(serverAddress)))
                                                         .description("Eevee DB")
                                                         .build();
        MongoClientSettings settings = MongoClientSettings.builder()
                                                          .codecRegistry(pojoCodecRegistry)
                                                          .applyToClusterSettings(block -> {
                                                              block.applySettings(clusterSettings);
                                                          })
                                                          .build();

        mongoClient = MongoClients.create(settings);

        return mongoClient;
    }

    public static void invalidate() {
        mongoClient = null;
    }
}
