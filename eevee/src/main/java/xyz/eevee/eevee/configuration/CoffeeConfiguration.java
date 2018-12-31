package xyz.eevee.eevee.configuration;

import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import xyz.eevee.coffee.client.CoffeeRPCClient;
import xyz.eevee.coffee.exc.CoffeeClientException;
import xyz.eevee.eevee.exc.InvalidConfigurationException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Builder
@Log4j2
public class CoffeeConfiguration implements Configuration {
    private CoffeeRPCClient coffeeRpcClient;
    private Map<String, Object> cache;
    private Map<String, Instant> lastFetch;
    private int cacheTtlSeconds;
    private Configuration overrides;
    private static final String NO_OVERRIDE_MSG = "Failed to read from override so defaulting to Coffee.";
    private static final String NO_COFFEE_SUPPORT_MSG = "Contact the Coffee team for int list support!";

    public void init() {
        coffeeRpcClient.init();
        cache = new ConcurrentHashMap<>();
        lastFetch = new ConcurrentHashMap<>();
    }

    @Override
    public Object readObject(@NonNull String key) throws InvalidConfigurationException {
        throw new UnsupportedOperationException("Use the typed methods!");
    }

    @Override
    public String readString(@NonNull String key) {
        if (overrides != null) {
            try {
                return overrides.readString(key);
            } catch (InvalidConfigurationException e) {
                log.debug(NO_OVERRIDE_MSG);
            }
        }

        return (String) hitOrFetch(key, () -> coffeeRpcClient.getString(key));
    }

    @Override
    public int readInt(@NonNull String key) {
        return (int) readDouble(key);
    }

    @Override
    public double readDouble(@NonNull String key) {
        if (overrides != null) {
            try {
                return overrides.readDouble(key);
            } catch (InvalidConfigurationException e) {
                log.debug(NO_OVERRIDE_MSG);
            }
        }

        return (double) hitOrFetch(key, () -> coffeeRpcClient.getNumber(key));
    }

    @Override
    public boolean readBoolean(@NonNull String key) {
        if (overrides != null) {
            try {
                return overrides.readBoolean(key);
            } catch (InvalidConfigurationException e) {
                log.debug(NO_OVERRIDE_MSG);
            }
        }

        return (boolean) hitOrFetch(key, () -> coffeeRpcClient.getBoolean(key));
    }

    @Override
    public List<String> readStringList(@NonNull String key) {
        if (overrides != null) {
            try {
                return overrides.readStringList(key);
            } catch (InvalidConfigurationException e) {
                log.debug(NO_OVERRIDE_MSG);
            }
        }

        return (List<String>) hitOrFetch(key, () -> coffeeRpcClient.getStringList(key));
    }

    @Override
    public List<Integer> readIntList(@NonNull String key) {
        throw new UnsupportedOperationException(NO_COFFEE_SUPPORT_MSG);
    }

    @Override
    public List<Double> readDoubleList(@NonNull String key) {
        throw new UnsupportedOperationException(NO_COFFEE_SUPPORT_MSG);
    }

    @Override
    public List<Boolean> readBooleanList(@NonNull String key) {
        throw new UnsupportedOperationException(NO_COFFEE_SUPPORT_MSG);
    }

    private Object hitOrFetch(@NonNull String key, @NonNull Supplier<Object> supplier) {
        boolean shouldFetch = true;
        Instant now = Instant.now();

        if (lastFetch.containsKey(key)) {
            Instant lastFetchTime = lastFetch.get(key);
            if (lastFetchTime.plusSeconds(cacheTtlSeconds).isAfter(now)) {
                shouldFetch = false;
            }
        }

        if (key.equalsIgnoreCase(GlobalConfiguration.COFFEE_PING_KEY) || shouldFetch) {
            try {
                cache.put(key, supplier.get());
                lastFetch.put(key, now);
            } catch (CoffeeClientException e) {
                log.warn("Failed to refresh config from Coffee. A stale config will be returned.", e);

                if (key.equalsIgnoreCase(GlobalConfiguration.COFFEE_PING_KEY)) {
                    throw new InvalidConfigurationException("Coffee is down throw an error to show on stats.");
                }

                if (!cache.containsKey(key)) {
                    throw new InvalidConfigurationException(
                        String.format("There is no cached config to return and Coffee is down: %s", key)
                    );
                }
            }
        }

        return cache.get(key);
    }

    public List<String> invalidateCache() {
        List<String> invalidatedKeys = ImmutableList.copyOf(cache.keySet());
        cache.clear();
        lastFetch.clear();
        return invalidatedKeys;
    }
}
