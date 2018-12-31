package xyz.eevee.eevee.rss;

import com.google.common.collect.ImmutableList;
import common.util.NetUtil;
import lombok.extern.log4j.Log4j2;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import xyz.eevee.eevee.rss.model.HorribleSubsReleaseItem;
import xyz.eevee.eevee.session.Session;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Log4j2
public class HorribleSubsReleaseReader extends ReleaseReader<HorribleSubsReleaseItem> {
    /**
     * Reads the HorribleSubs release RSS feed and returns a list containing HorribleSubsReleaseItem object
     * representations for each release item.
     *
     * @return An optional list of release items fetched from the current HS release feed. If an error occurs while
     * fetching the items then an empty Optional is returned. If the feed is successfully read but there are no items
     * then the optional will contain an empty list.
     */
    public Optional<List<HorribleSubsReleaseItem>> readFeed() {
        List<HorribleSubsReleaseItem> releases = new LinkedList<>();

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(
                NetUtil.streamPage(
                    Session.getSession().getConfiguration().readString(
                        "eevee.horribleSubsReleaseFeedURL"
                    )
                )
            );
            NodeList items = document.getElementsByTagName("item");

            for (int n = 0; n < items.getLength(); n++) {
                Element item = (Element) items.item(n);

                releases.add(
                    HorribleSubsReleaseItem.builder()
                                           .title(getValue(item, "title"))
                                           .guid(getValue(item, "guid"))
                                           .link(getValue(item, "link"))
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