package xyz.eevee.eevee.provider;

import java.util.UUID;

public class UuidProvider {
    /**
     * Generates a random UUID4.
     *
     * @return Generates a random UUID4 and returns it as a string.
     */
    public static String getUuid4() {
        return UUID.randomUUID().toString();
    }
}
