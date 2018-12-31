package xyz.eevee.eevee.provider.model.jisho;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Link {
    @JsonProperty
    private String text;
    @JsonProperty
    private String url;
}
