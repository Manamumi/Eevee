package xyz.eevee.eevee.provider.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.eevee.eevee.provider.model.jisho.Meta;
import xyz.eevee.eevee.provider.model.jisho.ResultData;

import java.util.List;

@Data
@NoArgsConstructor
public class JishoSearchResult {
    @JsonProperty
    private Meta meta;
    @JsonProperty
    private List<ResultData> data;
}
