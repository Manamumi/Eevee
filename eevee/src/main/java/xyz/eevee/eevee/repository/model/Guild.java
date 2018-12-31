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
public class Guild {
    private ObjectId id;
    @NonNull
    private String serverId;
    @NonNull
    private String guildName;
    private String welcomeMessage;
    private String welcomeChannelId;
}
