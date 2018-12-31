package xyz.eevee.eevee.repository;

import com.google.common.collect.ImmutableList;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import xyz.eevee.eevee.provider.MongoClientProvider;
import xyz.eevee.eevee.repository.model.Reminder;
import xyz.eevee.eevee.session.Session;

import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

@Log4j2
public class ReminderDataRepository extends DataRepository {
    private static ReminderDataRepository dataRepository;
    private final MongoCollection<Reminder> mongoCollection;

    private ReminderDataRepository() {
        super();
        MongoClient mongoClient = MongoClientProvider.getInstance();
        String collectionName = Session.getSession().getConfiguration().readString("eevee.mongoReminderCollectionName");
        String databaseName = Session.getSession().getConfiguration().readString("eevee.mongoDatabaseName");
        mongoCollection = mongoClient.getDatabase(databaseName)
                                     .getCollection(collectionName, Reminder.class);
    }

    public static ReminderDataRepository getInstance() {
        if (dataRepository == null) {
            dataRepository = new ReminderDataRepository();
        }

        return dataRepository;
    }

    public static ReminderDataRepository reload() {
        return (dataRepository = new ReminderDataRepository());
    }

    public List<Reminder> getReminders() {
        return ImmutableList.copyOf(mongoCollection.find());
    }

    public void add(@NonNull Reminder reminder) {
        mongoCollection.insertOne(reminder);
    }

    public void remove(@NonNull Reminder reminder) {
        mongoCollection.deleteOne(
            and(
                eq("userId", reminder.getUserId()),
                eq("remindAt", reminder.getRemindAt()),
                eq("reminder", reminder.getReminder())
            )
        );
    }
}
