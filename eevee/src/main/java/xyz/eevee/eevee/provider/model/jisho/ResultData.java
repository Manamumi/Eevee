package xyz.eevee.eevee.provider.model.jisho;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ResultData {
    @JsonProperty("is_common")
    private boolean isCommon;
    @JsonProperty
    private List<String> tags;
    @JsonProperty
    List<Japanese> japanese;
    @JsonProperty
    List<Sense> senses;
    @JsonProperty
    Attribution attribution;
}
