package xyz.eevee.coffee.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Builder
@Log4j2
class DataReader {
    private ObjectMapper objectMapper;
    private File dataFile;

    Optional<Node> read() {
        try {
            return Optional.of(objectMapper.readValue(dataFile, Node.class));
        } catch (IOException e) {
            log.error("Unable to load data from datafile.", e);
        }

        return Optional.empty();
    }
}
