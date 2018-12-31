package xyz.eevee.coffee.data;

import xyz.eevee.coffee.rpc.EntryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Node {
    private static final String KEY_SEPARATOR = ".";

    private String key;
    private Object value;
    @Builder.Default
    private Map<String, Node> children = new ConcurrentHashMap<>();
    @Builder.Default
    private EntryType entryType = EntryType.String;

    synchronized Optional<Node> get(List<String> keyParts) {
        Node currentNode = this;

        for (String part : keyParts) {
            if (!currentNode.getChildren().containsKey(part)) {
                return Optional.empty();
            }

            currentNode = currentNode.getChildren().get(part);
        }

        return Optional.of(currentNode);
    }

    synchronized Node set(List<String> keyParts, Object value, EntryType entryType) {
        Node currentNode = this;
        StringBuilder stringBuilder = new StringBuilder();

        for (String part : keyParts) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append('.');
            }

            stringBuilder.append(part);

            if (!currentNode.getChildren().containsKey(part)) {
                currentNode.getChildren().put(
                        part,
                        Node.builder()
                            .key(stringBuilder.toString())
                            .build()
                );
            }

            currentNode = currentNode.getChildren().get(part);
        }

        currentNode.setEntryType(entryType);
        currentNode.setValue(value);

        return currentNode;
    }

    synchronized Optional<Node> remove(List<String> keyParts) {
        Node currentNode = this;

        for (int n = 0; n < keyParts.size() - 1; n++) {
            String part = keyParts.get(n);

            if (!currentNode.getChildren().containsKey(part)) {
                return Optional.empty();
            }

            currentNode = currentNode.getChildren().get(part);
        }

        String lastPart = keyParts.get(keyParts.size() - 1);

        return Optional.ofNullable(currentNode.getChildren().remove(lastPart));
    }
}
