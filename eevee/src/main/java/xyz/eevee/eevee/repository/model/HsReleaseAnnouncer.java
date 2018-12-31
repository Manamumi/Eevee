package xyz.eevee.eevee.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HsReleaseAnnouncer {
    private ObjectId objectId;
    @NonNull
    private String announcerId;
    @NonNull
    private String channelId;
    @NonNull
    private String anime;
    @NonNull
    private String quality;
    private int lastEpisode;
    @Builder.Default
    private boolean download = false;
}
