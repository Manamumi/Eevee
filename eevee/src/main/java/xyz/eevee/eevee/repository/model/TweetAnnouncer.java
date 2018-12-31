package xyz.eevee.eevee.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;

import java.time.Instant;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TweetAnnouncer {
    private ObjectId objectId;
    @NonNull
    private String announcerId;
    @NonNull
    private String channelId;
    @NonNull
    private String user;
    private long lastTweetId;
    private Instant lastTweetTimestamp;
}
