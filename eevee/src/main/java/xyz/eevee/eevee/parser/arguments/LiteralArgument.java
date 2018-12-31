package xyz.eevee.eevee.parser.arguments;

import lombok.NonNull;
import xyz.eevee.eevee.parser.Tokenizer;
import xyz.eevee.munchlax.NewMessageEvent;

public class LiteralArgument extends Argument {
    private String value;

    public LiteralArgument(@NonNull String value) {
        super("FOO");
        this.value = value;
    }

    @Override
    public boolean isValid(@NonNull Tokenizer tokens, @NonNull NewMessageEvent message) {
        String token = tokens.next();
        return token != null && token.equalsIgnoreCase(value);
    }

    @Override
    public Object parse(@NonNull Tokenizer tokens, @NonNull NewMessageEvent message) {
        return tokens.next();
    }

    @Override
    public String toString() {
        return value;
    }
}
