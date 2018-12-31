package xyz.eevee.eevee.provider.model.jisho;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Attribution {
    @JsonProperty
    private boolean jmdict;
    @JsonProperty
    private boolean jmnedict;
    @JsonProperty
    private String dbpedia;
}
