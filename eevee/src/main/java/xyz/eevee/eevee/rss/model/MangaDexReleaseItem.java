package xyz.eevee.eevee.rss.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MangaDexReleaseItem {
    private String title;
    private String link;
    private String description;
    private String pubDate;
}
