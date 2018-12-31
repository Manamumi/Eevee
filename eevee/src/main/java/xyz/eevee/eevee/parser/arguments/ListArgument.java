package xyz.eevee.eevee.parser.arguments;

import lombok.NonNull;
import xyz.eevee.eevee.parser.Tokenizer;
import xyz.eevee.munchlax.NewMessageEvent;

import java.util.LinkedList;
import java.util.List;

public class ListArgument<BaseT extends Argument, ReturnT> extends Argument {
    private List<BaseT> values;

    public ListArgument(String name, @NonNull List<BaseT> arguments) {
        super(name);
        this.values = arguments;
    }

    @Override
    public boolean isValid(@NonNull Tokenizer tokens, @NonNull NewMessageEvent message) {
        for (BaseT argument : values) {
            if (!argument.isValid(tokens, message)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public List<ReturnT> parse(@NonNull Tokenizer tokens, @NonNull NewMessageEvent message) {
        List<ReturnT> resultsList = new LinkedList<>();

        for (BaseT argument : values) {
            resultsList.add((ReturnT) argument.parse(tokens, message));
        }

        return resultsList;
    }

    @Override
    public String toString() {
        String returnString = String.format("<%s | [", getName());
        String types = String.join(" ", (String[]) values.stream().map(Object::toString).toArray());
        return String.format("%s%s]>", returnString, types);
    }
}
