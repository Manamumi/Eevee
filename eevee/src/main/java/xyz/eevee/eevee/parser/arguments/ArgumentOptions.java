package xyz.eevee.eevee.parser.arguments;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class ArgumentOptions {
    private boolean required;
    private Object defaultValue;
}
