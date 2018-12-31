package xyz.eevee.eevee.rss;

import lombok.NonNull;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Optional;

public abstract class ReleaseReader<T> {
    /**
     * Reads a RSS release feed and returns a list containing release object
     * representations for each release item.
     *
     * @return An optional list of release items fetched from the current release feed. If an error occurs while
     * fetching the items then an empty Optional is returned. If the feed is successfully read but there are no items
     * then the optional will contain an empty list.
     */
    public abstract Optional<List<T>> readFeed();

    protected String getValue(@NonNull Element parent, @NonNull String nodeName) {
        return parent.getElementsByTagName(nodeName).item(0).getFirstChild().getTextContent();
    }
}
