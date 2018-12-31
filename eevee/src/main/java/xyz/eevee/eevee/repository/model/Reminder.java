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
public class Reminder {
    private ObjectId id;
    @NonNull
    private String userTag;
    @NonNull
    private String userId;
    @NonNull
    private String reminder;
    @NonNull
    private Instant remindAt;
}
