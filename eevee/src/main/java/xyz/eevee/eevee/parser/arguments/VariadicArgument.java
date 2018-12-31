package xyz.eevee.eevee.parser.arguments;

import lombok.NonNull;
import xyz.eevee.eevee.parser.Tokenizer;
import xyz.eevee.munchlax.NewMessageEvent;

import java.util.LinkedList;
import java.util.List;

public class VariadicArgument<BaseT extends Argument, ReturnT> extends Argument {
    private BaseT dummy;

    public VariadicArgument(String name, @NonNull BaseT dummy) {
        super(name);
        this.dummy = dummy;
    }

    @Override
    public boolean isValid(@NonNull Tokenizer tokens, @NonNull NewMessageEvent message) {
        while (tokens.hasNext()) {
            if (!dummy.isValid(tokens, message)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public List<ReturnT> parse(@NonNull Tokenizer tokens, @NonNull NewMessageEvent message) {
        List<ReturnT> arguments = new LinkedList<>();

        while (tokens.hasNext()) {
            arguments.add((ReturnT) dummy.parse(tokens, message));
        }

        return arguments;
    }

    @Override
    public String toString() {
        return String.format("<%s | variadic (%s)>", getName(), dummy.getClass().getSimpleName());
    }
}
