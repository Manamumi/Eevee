package xyz.eevee.eevee.configuration;

import lombok.NonNull;
import xyz.eevee.eevee.exc.InvalidConfigurationException;

import java.util.List;

public interface Configuration {
    Object readObject(@NonNull String key) throws InvalidConfigurationException;

    String readString(@NonNull String key) throws InvalidConfigurationException;

    int readInt(@NonNull String key) throws InvalidConfigurationException;

    double readDouble(@NonNull String key) throws InvalidConfigurationException;

    boolean readBoolean(@NonNull String key) throws InvalidConfigurationException;

    List<String> readStringList(@NonNull String key) throws InvalidConfigurationException;

    List<Integer> readIntList(@NonNull String key) throws InvalidConfigurationException;

    List<Double> readDoubleList(@NonNull String key) throws InvalidConfigurationException;

    List<Boolean> readBooleanList(@NonNull String key) throws InvalidConfigurationException;
}
