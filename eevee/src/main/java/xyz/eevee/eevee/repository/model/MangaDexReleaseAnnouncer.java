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
public class MangaDexReleaseAnnouncer {
    private ObjectId objectId;
    @NonNull
    private String announcerId;
    @NonNull
    private String channelId;
    @NonNull
    private String title;
    @NonNull
    private String scanlator;
    private Instant lastChapter;
}