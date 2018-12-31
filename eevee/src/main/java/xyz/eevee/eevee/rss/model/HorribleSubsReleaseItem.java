package xyz.eevee.eevee.rss.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class HorribleSubsReleaseItem {
    private String title;
    private String link;
    private String guid;
    private String pubDate;
}
