package xyz.eevee.eevee.configuration;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import xyz.eevee.eevee.exc.InvalidConfigurationException;
import xyz.eevee.eevee.exc.InvalidConfigurationKeyException;
import xyz.eevee.eevee.session.Session;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
@Log4j2
public class GenericJsonConfiguration implements Configuration {
    @NonNull
    private InputStream fileStream;
    private Map<String, Object> data;
    /**
     * Setting isOverride to true will suppress all error messages
     * as it is expected for "invalid keys" to appear for typical
     * use-cases. Exceptional cases such as IOException due to
     * a lack of read permission will still error.
     */
    @Builder.Default
    private boolean isOverride;
    private String prefix;
    private static final String INVALID_JSON_MSG = "The configuration could not be parsed as valid JSON.";

    public Object readObject(@NonNull String key) throws InvalidConfigurationException {
        if (data == null) {
            loadData();
        }

        String[] parts = key.split("\\.");
        Map<String, Object> tier = data;

        for (int n = 0; n < parts.length - 1; n++) {
            try {
                if (tier.containsKey(parts[n])) {
                    tier = (Map<String, Object>) tier.get(parts[n]);
                } else {
                    if (!isOverride) {
                        log.error(String.format("Attempted to read String for invalid key: %s.", key));
                    }

                    throw new InvalidConfigurationKeyException(
                        String.format("The requested key \"%s\" does not exist.", key)
                    );
                }
            } catch (Exception e) {
                throw new InvalidConfigurationKeyException(
                    String.format("Unexpected value typing for key \"%s\".", key)
                );
            }
        }

        if (tier.containsKey(parts[parts.length - 1])) {
            log.debug(String.format("Found value for key: %s. Attempting to return.", key));
            return tier.get(parts[parts.length - 1]);
        } else {
            if (!isOverride) {
                log.error(String.format("Attempted to read String for invalid key: %s.", key));
            }

            throw new InvalidConfigurationKeyException(
                String.format("The requested key \"%s\" does not exist.", key)
            );
        }
    }

    public String readString(@NonNull String key) throws InvalidConfigurationException {
        return (String) readObject(key);
    }

    public int readInt(@NonNull String key) throws InvalidConfigurationException {
        return (Integer) readObject(key);
    }

    public double readDouble(@NonNull String key) throws InvalidConfigurationException {
        return (Double) readObject(key);
    }

    public boolean readBoolean(@NonNull String key) throws InvalidConfigurationException {
        return (Boolean) readObject(key);
    }

    public List<String> readStringList(@NonNull String key) throws InvalidConfigurationException {
        return (List<String>) readObject(key);
    }

    public List<Integer> readIntList(@NonNull String key) throws InvalidConfigurationException {
        return (List<Integer>) readObject(key);
    }

    public List<Double> readDoubleList(@NonNull String key) throws InvalidConfigurationException {
        return (List<Double>) readObject(key);
    }

    public List<Boolean> readBooleanList(@NonNull String key) throws InvalidConfigurationException {
        return (List<Boolean>) readObject(key);
    }

    private void loadData() throws InvalidConfigurationException {
        try {
            ObjectMapper objectMapper = Session.getSession().getObjectMapper();
            TypeReference<HashMap<String, Object>> genericTypeReference = new TypeReference<HashMap<String, Object>>() {
            };
            data = objectMapper.<HashMap<String, Object>>readValue(fileStream, genericTypeReference);
        } catch (JsonParseException e) {
            log.error(
                "Error mapping configuration values. " + INVALID_JSON_MSG
            );
            throw new InvalidConfigurationException(INVALID_JSON_MSG);
        } catch (JsonMappingException e) {
            log.error("Error mapping configuration values. " + INVALID_JSON_MSG);
            throw new InvalidConfigurationException("Error mapping configuration values. " + INVALID_JSON_MSG);
        } catch (IOException e) {
            log.error(
                "Unexpected IO exception occurred. The configuration could not be loaded."
            );
            throw new InvalidConfigurationException(
                "Unexpected IO exception occurred."
            );
        }
    }
}
