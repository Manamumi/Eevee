package xyz.eevee.eevee.parser.arguments;

import xyz.eevee.eevee.parser.Tokenizer;
import xyz.eevee.munchlax.NewMessageEvent;

import java.util.List;

public class OrArgument extends Argument {
    private List<String> options;

    public OrArgument(String name, List<String> options) {
        super(name);
        this.options = options;
    }

    @Override
    public boolean isValid(Tokenizer tokens, NewMessageEvent message) {
        String token = tokens.next();

        for (String option : options) {
            if (option.equalsIgnoreCase(token)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Object parse(Tokenizer tokens, NewMessageEvent message) {
        return tokens.next();
    }

    @Override
    public String toString() {
        return String.format("<%s | [%s]>", getName(), String.join(", ", options));
    }
}
