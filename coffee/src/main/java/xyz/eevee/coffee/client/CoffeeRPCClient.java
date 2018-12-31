package xyz.eevee.coffee.client;

import com.google.common.collect.ImmutableList;
import common.gateway.RPCGateway;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.NonNull;
import xyz.eevee.coffee.exc.CoffeeClientException;
import xyz.eevee.coffee.rpc.*;
import xyz.eevee.coffee.util.DataTransformUtil;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Log4j2
@Builder
public class CoffeeRPCClient {
    @NonNull
    private String coffeeHost;
    private int coffeePort;
    @Builder.Default
    private int delayMilli = 25;
    @Builder.Default
    private int backoffRatio = 2;
    @Builder.Default
    private int maxAttempts = 3;
    private String keyPrefix;
    @NonNull
    private String insideAppToken;

    private CoffeeGrpc.CoffeeBlockingStub blockingStub;

    public void init() {
        if (coffeeHost == null || coffeePort == 0) {
            throw new CoffeeClientException("Coffee host and port must be specified.");
        }

        ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder.forAddress(
            coffeeHost, coffeePort
        ).usePlaintext(true);
        ManagedChannel channel = channelBuilder.build();
        blockingStub = CoffeeGrpc.newBlockingStub(channel);
        blockingStub = RPCGateway.attachHeaders(blockingStub, insideAppToken);
    }

    public String getString(String key) {
        return retryableAction(() -> readString(key).get());
    }

    public double getNumber(String key) {
        return retryableAction(() -> readNumber(key).get());
    }

    public boolean getBoolean(String key) {
        return retryableAction(() -> readBoolean(key).get());
    }

    public List<String> getStringList(String key) {
        return retryableAction(() -> readStringList(key).get());
    }

    public Map<String, Object> batchGet(List<String> keys) {
        return retryableAction(() -> batchRead(keys).get());
    }

    public CompletableFuture<String> readString(String key) {
        if (blockingStub == null) {
            init();
        }

        return CompletableFuture.supplyAsync(() ->
            blockingStub.getString(
                keyFromString(key)
            )
        ).thenApply(entry -> {
            if (entry.getError() != null && entry.getError().length() != 0) {
                throw new CoffeeClientException(entry.getError());
            }

            return entry;
        }).thenApply(StringEntry::getValue);
    }

    public CompletableFuture<Double> readNumber(String key) {
        if (blockingStub == null) {
            init();
        }

        return CompletableFuture.supplyAsync(() ->
            blockingStub.getNumber(
                keyFromString(key)
            )
        ).thenApply(entry -> {
            if (entry.getError() != null && entry.getError().length() != 0) {
                log.error(entry.getError());
                throw new CoffeeClientException(entry.getError());
            }

            return entry;
        }).thenApply(NumberEntry::getValue);
    }

    public CompletableFuture<Boolean> readBoolean(String key) {
        if (blockingStub == null) {
            init();
        }

        return CompletableFuture.supplyAsync(() ->
            blockingStub.getBoolean(
                keyFromString(key)
            )
        ).thenApply(entry -> {
            if (entry.getError() != null && entry.getError().length() != 0) {
                log.error(entry.getError());
                throw new CoffeeClientException(entry.getError());
            }

            return entry;
        }).thenApply(BooleanEntry::getValue);
    }

    public CompletableFuture<List<String>> readStringList(String key) {
        if (blockingStub == null) {
            init();
        }

        return CompletableFuture.supplyAsync(() ->
            blockingStub.getStringList(
                keyFromString(key)
            )
        ).thenApply(entry -> {
            if (entry.getError() != null && entry.getError().length() != 0) {
                log.error(entry.getError());
                throw new CoffeeClientException(entry.getError());
            }

            return entry;
        }).thenApply(StringListEntry::getValueList);
    }

    public CompletableFuture<Map<String, Object>> batchRead(List<String> keys) {
        if (blockingStub == null) {
            init();
        }

        Map<String, Object> resultMap = new ConcurrentHashMap<>();

        CompletableFuture[] completableFutures = (CompletableFuture[]) keys.stream().map(keyString ->
            CompletableFuture.supplyAsync(
                () -> blockingStub.get(keyFromString(keyString))
            ).thenApply(entry -> {
                if (entry.getError() != null && entry.getError().length() != 0) {
                    log.error(entry.getError());
                    throw new CoffeeClientException(entry.getError());
                }

                return entry;
            }).thenAccept(entry -> {
                Object value = entry.getValue();

                if (entry.getEntryType() == EntryType.StringList) {
                    value = DataTransformUtil.transformToStringList(value);
                }

                resultMap.put(String.join(".", entry.getKeyList()), value);
            })
        ).toArray();

        return CompletableFuture.allOf(completableFutures)
                                .thenApply(x -> resultMap);
    }

    public void setString(String key, String value) {
        retryableAction(() -> writeString(key, value).get());
    }

    public void setNumber(String key, double value) {
        retryableAction(() -> writeNumber(key, value).get());
    }

    public void setBoolean(String key, boolean value) {
        retryableAction(() -> writeBoolean(key, value).get());
    }

    public void setStringList(String key, List<String> value) {
        retryableAction(() -> writeStringList(key, value).get());
    }

    public CompletableFuture<WriteResult> writeString(String key, String value) {
        if (blockingStub == null) {
            init();
        }

        return CompletableFuture.supplyAsync(() ->
            blockingStub.setString(
                StringEntry.newBuilder()
                           .addAllKey(generateKey(key))
                           .setValue(value)
                           .build()
            )
        ).thenApply(result -> {
            if (result.getError() != null && result.getError().length() != 0) {
                throw new CoffeeClientException(result.getError());
            }

            return result;
        });
    }

    public CompletableFuture<WriteResult> writeNumber(String key, double value) {
        if (blockingStub == null) {
            init();
        }

        return CompletableFuture.supplyAsync(() ->
            blockingStub.setNumber(
                NumberEntry.newBuilder()
                           .addAllKey(generateKey(key))
                           .setValue(value)
                           .build()
            )
        ).thenApply(result -> {
            if (result.getError() != null && result.getError().length() != 0) {
                throw new CoffeeClientException(result.getError());
            }

            return result;
        });
    }

    public CompletableFuture<WriteResult> writeBoolean(String key, boolean value) {
        if (blockingStub == null) {
            init();
        }

        return CompletableFuture.supplyAsync(() ->
            blockingStub.setBoolean(
                BooleanEntry.newBuilder()
                            .addAllKey(generateKey(key))
                            .setValue(value)
                            .build()
            )
        ).thenApply(result -> {
            if (result.getError() != null && result.getError().length() != 0) {
                throw new CoffeeClientException(result.getError());
            }

            return result;
        });
    }

    public CompletableFuture<Entry> delete(String key) {
        if (blockingStub == null) {
            init();
        }

        return CompletableFuture.supplyAsync(() ->
            blockingStub.delete(
                keyFromString(key)
            )
        ).thenApply(result -> {
            if (result.getError() != null && result.getError().length() != 0) {
                throw new CoffeeClientException(result.getError());
            }

            return result;
        });
    }

    public CompletableFuture<WriteResult> writeStringList(String key, List<String> value) {
        if (blockingStub == null) {
            init();
        }

        return CompletableFuture.supplyAsync(() ->
            blockingStub.setStringList(
                StringListEntry.newBuilder()
                               .addAllKey(generateKey(key))
                               .addAllValue(value)
                               .build()
            )
        ).thenApply(result -> {
            if (result.getError() != null && result.getError().length() != 0) {
                throw new CoffeeClientException(result.getError());
            }

            return result;
        });
    }

    private <T> T retryableAction(ConcurrentSupplier<T> readFunc) {
        int currentAttempt = 0;
        int currentDelayMilli = delayMilli;

        while (currentAttempt < maxAttempts) {
            try {
                return readFunc.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error(
                    String.format(
                        "Failed to read from Coffee server. This is attempt %s of %s.",
                        currentAttempt + 1,
                        maxAttempts
                    ),
                    e
                );

                currentAttempt++;
                try {
                    Thread.sleep(currentDelayMilli);
                } catch (InterruptedException e1) {
                    log.error("Failed to sleep during retry backoff.", e1);
                }
                currentAttempt *= backoffRatio;
            }
        }

        throw new CoffeeClientException("Failed to read from Coffee server.");
    }

    private Key keyFromString(String key) {
        return Key.newBuilder().addAllValue(generateKey(key)).build();
    }

    private List<String> generateKey(String key) {
        List<String> keyList = new LinkedList<>(
            Arrays.asList(
                key.split("\\.")
            )
        );

        if (keyPrefix != null) {
            keyList.add(0, keyPrefix);
        }

        return ImmutableList.copyOf(keyList);
    }

    interface ConcurrentSupplier<T> {
        T get() throws InterruptedException, ExecutionException;
    }
}
