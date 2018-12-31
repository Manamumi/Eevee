package xyz.eevee.eevee.parser.arguments;

import lombok.NonNull;
import xyz.eevee.eevee.parser.Tokenizer;
import xyz.eevee.munchlax.NewMessageEvent;

public class StringArgument extends Argument {
    public StringArgument(String name) {
        super(name);
    }

    @Override
    public boolean isValid(@NonNull Tokenizer tokens, @NonNull NewMessageEvent message) {
        // Consume a token.
        tokens.next();
        return true;
    }

    @Override
    public Object parse(@NonNull Tokenizer tokens, @NonNull NewMessageEvent message) {
        return tokens.next();
    }

    @Override
    public String toString() {
        return String.format("<%s | string>", getName());
    }
}
