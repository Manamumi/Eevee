package xyz.eevee.eevee.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenericStringList {
    private ObjectId objectId;
    @NonNull
    private String key;
    @NonNull
    private List<String> list;
}
