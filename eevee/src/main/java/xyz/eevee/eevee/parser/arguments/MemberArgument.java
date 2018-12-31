package xyz.eevee.eevee.parser.arguments;

import lombok.NonNull;
import xyz.eevee.eevee.parser.Tokenizer;
import xyz.eevee.munchlax.NewMessageEvent;
import xyz.eevee.munchlax.Member;

public class MemberArgument extends Argument {
    public MemberArgument(String name) {
        super(name);
    }

    @Override
    public boolean isValid(@NonNull Tokenizer tokens, @NonNull NewMessageEvent message) {
        String token = tokens.next();

        if (!token.matches("<@!?\\d+>")) {
            return false;
        }

        String memberId = token.replaceAll("(<@!?)|>", "");

        return message.getMentionsList()
                      .stream()
                      .anyMatch(m -> m.getUser().getId().equals(memberId));
    }

    @Override
    public Member parse(@NonNull Tokenizer tokens, @NonNull NewMessageEvent message) {
        String token = tokens.next();
        String memberId = token.replaceAll("(<@!?)|>", "");

        return message.getMentionsList()
                      .stream()
                      .filter(user -> user.getUser().getId().equals(memberId))
                      .findFirst().get();
    }

    @Override
    public String toString() {
        return String.format("<%s | member mention>", getName());
    }
}
