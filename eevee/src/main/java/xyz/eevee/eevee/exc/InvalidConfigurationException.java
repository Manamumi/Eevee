package xyz.eevee.eevee.exc;

import lombok.NonNull;

public class InvalidConfigurationException extends RuntimeException {
    public InvalidConfigurationException(@NonNull String message) {
        super(message);
    }
}
