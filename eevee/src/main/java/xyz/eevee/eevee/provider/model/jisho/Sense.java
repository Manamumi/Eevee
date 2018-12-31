package xyz.eevee.eevee.provider.model.jisho;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Sense {
    @JsonProperty("english_definitions")
    private List<String> englishDefinitions;
    @JsonProperty("parts_of_speech")
    private List<String> partsOfSpeech;
    @JsonProperty
    private List<Link> links;
    @JsonProperty
    private List<String> tags;
    @JsonProperty
    private List<String> restrictions;
    @JsonProperty("see_also")
    private List<String> seeAlso;
    @JsonProperty
    private List<String> antonyms;
    @JsonProperty
    private List<Source> source;
    @JsonProperty
    private List<String> info;
    @JsonProperty
    private List<String> sentences;
}
