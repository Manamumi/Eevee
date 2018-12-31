package xyz.eevee.eevee.rss;

import com.google.common.collect.ImmutableList;
import common.util.NetUtil;
import lombok.extern.log4j.Log4j2;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import xyz.eevee.eevee.rss.model.MangaDexReleaseItem;
import xyz.eevee.eevee.session.Session;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Log4j2
public class MangaDexReleaseReader extends ReleaseReader<MangaDexReleaseItem> {
    @Override
    public Optional<List<MangaDexReleaseItem>> readFeed() {
        List<MangaDexReleaseItem> releases = new LinkedList<>();

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(
                NetUtil.streamPage(
                    String.format(
                        Session.getSession().getConfiguration().readString("eevee.mangadexReleaseFeedUrl"),
                        Session.getSession().getConfiguration().readString("eevee.mangadexRssKey")
                    )
                )
            );
            NodeList items = document.getElementsByTagName("item");

            for (int n = 0; n < items.getLength(); n++) {
                Element item = (Element) items.item(n);

                releases.add(
                    MangaDexReleaseItem.builder()
                                       .title(getValue(item, "title"))
                                       .link(getValue(item, "link"))
                                       .description(getValue(item, "description"))
                                       .pubDate(getValue(item, "pubDate"))
                                       .build()
                );
            }

            return Optional.of(ImmutableList.copyOf(releases));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            log.error("Failed to create XML parser.", e);
        } catch (SAXException e) {
            e.printStackTrace();
            log.error("Failed to parse release XML.", e);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Failed to get release XML", e);
        }

        return Optional.empty();
    }
}
