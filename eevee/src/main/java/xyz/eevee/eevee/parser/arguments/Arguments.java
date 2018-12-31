package xyz.eevee.eevee.parser.arguments;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import xyz.eevee.eevee.exc.ArgumentMappingException;
import xyz.eevee.eevee.parser.Tokenizer;
import xyz.eevee.munchlax.NewMessageEvent;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class Arguments<T> extends Argument {
    @Getter
    private List<Argument> arguments;
    private Class<T> mapClass;

    public Arguments(List<Argument> arguments, Class<T> mapClass) {
        this.arguments = arguments;
        this.mapClass = mapClass;
    }

    @Override
    public boolean isValid(@NonNull Tokenizer tokens, @NonNull NewMessageEvent message) {
        if (!isPartialValid(tokens, message)) {
            return false;
        }

        return !tokens.hasNext();
    }

    @Override
    public T parse(@NonNull Tokenizer tokens, @NonNull NewMessageEvent message) {
        try {
            T obj = mapClass.newInstance();
            parsePartial(tokens, message, obj);
            return obj;
        } catch (InstantiationException | IllegalAccessException | NoSuchFieldException e) {
            log.error("Attempted to set field that does not exist.", e);
            throw new ArgumentMappingException("Failed to parse and get command arguments.");
        }
    }

    private boolean isPartialValid(@NonNull Tokenizer tokens, @NonNull NewMessageEvent message) {
        for (Argument arg : arguments) {
            if (arg instanceof Arguments) {
                tokens.stash();

                boolean subArgumentsAreValid = ((Arguments) arg).isPartialValid(tokens, message);

                if (!subArgumentsAreValid) {
                    tokens.pop();

                    if (arg.getOptions().isRequired()) {
                        return false;
                    }
                }
            } else {
                if (!tokens.hasNext()) {
                    if (!arg.getOptions().isRequired()) {
                        continue;
                    }

                    return false;
                }

                if (!arg.getOptions().isRequired()) {
                    tokens.stash();
                }

                if (!arg.isValid(tokens, message)) {
                    if (arg.getOptions().isRequired()) {
                        return false;
                    } else {
                        tokens.pop();
                    }
                }
            }
        }

        return true;
    }

    private void parsePartial(
        @NonNull Tokenizer tokens,
        @NonNull NewMessageEvent message,
        @NonNull T obj
    ) throws NoSuchFieldException, IllegalAccessException {
        for (Argument arg : arguments) {
            if (!tokens.hasNext()) {
                return;
            }

            if (arg instanceof LiteralArgument) {
                tokens.stash();

                if (!arg.isValid(tokens, message)) {
                    tokens.pop();
                }
            } else if (arg instanceof Arguments) {
                tokens.stash();

                if (!((Arguments) arg).isPartialValid(tokens, message) && !arg.getOptions().isRequired()) {
                    tokens.pop();

                    List<Argument> subArguments = ((Arguments) arg).getArguments();

                    for (Argument subArgument : subArguments) {
                        Object defaultValue = subArgument.getOptions().getDefaultValue();

                        if (defaultValue == null) {
                            continue;
                        }

                        applyValue(obj, subArgument.getName(), subArgument.getOptions().getDefaultValue());
                    }
                } else {
                    tokens.pop();
                    ((Arguments) arg).parsePartial(tokens, message, obj);
                }
            } else {
                tokens.stash();

                if (arg.isValid(tokens, message)) {
                    tokens.pop();
                    applyValue(obj, arg.getName(), arg.parse(tokens, message));
                } else {
                    tokens.pop();

                    Object defaultValue = arg.getOptions().getDefaultValue();

                    if (defaultValue != null) {
                        applyValue(obj, arg.getName(), arg.getOptions().getDefaultValue());
                    }
                }
            }
        }
    }

    private void applyValue(
        @NonNull T obj,
        @NonNull String name,
        @NonNull Object value
    ) throws NoSuchFieldException, IllegalAccessException {
        Field field = obj.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(obj, value);
    }

    @Override
    public String toString() {
        return arguments.stream().map(a -> {
            if (!a.getOptions().isRequired()) {
                return String.format("[%s]", a.toString());
            }

            return a.toString();
        }).collect(Collectors.joining(" "));
    }
}
