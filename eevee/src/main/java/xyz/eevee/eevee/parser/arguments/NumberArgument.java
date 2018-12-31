package xyz.eevee.eevee.parser.arguments;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import xyz.eevee.eevee.parser.Tokenizer;
import xyz.eevee.munchlax.NewMessageEvent;

@Log4j2
public class NumberArgument extends Argument {
    public NumberArgument(String name) {
        super(name);
    }

    @Override
    public boolean isValid(@NonNull Tokenizer tokens, @NonNull NewMessageEvent message) {
        String token = tokens.next();

        if (token == null) {
            return false;
        }

        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public Double parse(@NonNull Tokenizer tokens, @NonNull NewMessageEvent message) {
        String token = tokens.next();

        try {
            double d = Double.parseDouble(token);
            return d;
        } catch (NumberFormatException e) {
            // This should never be reached.
            log.warn(
                "NumberFormatException encountered in NumberArgument#parse. This should never happen" +
                    "because we already passed NumberArgument#isValid!", e
            );
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("<%s | number>", getName());
    }
}
