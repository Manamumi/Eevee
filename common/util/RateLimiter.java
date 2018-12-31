package common.util;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Builder
public class RateLimiter {
    /**
     * The maximum number of times this rate limiter may be invoked before throttling.
     */
    @Getter
    private int maxHits;
    /**
     * The duration for which this rate limiter may be invoked maxHits number of times.
     * This should be in milliseconds.
     */
    @Getter
    private int duration;
    /**
     * Mapping of user ID to their last reset time.
     */
    @Builder.Default
    private Map<String, Instant> userLastResets = new HashMap<>();
    /**
     * Mapping of user ID to their current number of hits.
     */
    @Builder.Default
    private Map<String, Integer> userHits = new HashMap<>();

    /**
     * Attempts to invoke the rate limiter for a user id. If the limiter for a given ID has been maxed out then
     * false will be returned else true.
     *
     * @param uid A Discord user ID to test.
     * @return Whether or not the rate limiter was successfully invoked.
     */
    public boolean tryIncrement(@NonNull String uid) {
        Instant lastReset = userLastResets.get(uid);

        if (lastReset == null) {
            lastReset = Instant.now();
            userLastResets.put(uid, lastReset);
            userHits.put(uid, 0);
        }

        Instant now = Instant.now();

        if (now.isAfter(lastReset.plusMillis(duration))) {
            lastReset = now;
            userLastResets.put(uid, lastReset);
            userHits.put(uid, 0);
        }

        int currentHits = userHits.get(uid);

        if (currentHits < maxHits) {
            userHits.put(uid, currentHits + 1);
            return true;
        } else {
            return false;
        }
    }
}
