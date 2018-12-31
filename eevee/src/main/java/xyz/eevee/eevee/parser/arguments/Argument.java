package xyz.eevee.eevee.parser.arguments;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import xyz.eevee.eevee.parser.Tokenizer;
import xyz.eevee.munchlax.NewMessageEvent;

@NoArgsConstructor
@RequiredArgsConstructor
public abstract class Argument {
    @NonNull
    @Getter
    private String name;
    @Getter
    @Setter
    private ArgumentOptions options = ArgumentOptions.builder()
                                                     .required(true)
                                                     .build();

    public Argument withOptions(@NonNull ArgumentOptions options) {
        setOptions(options);
        return this;
    }

    public abstract boolean isValid(@NonNull Tokenizer tokens, @NonNull NewMessageEvent message);

    public abstract Object parse(@NonNull Tokenizer tokens, @NonNull NewMessageEvent message);
}
