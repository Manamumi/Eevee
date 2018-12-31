package xyz.eevee.eevee.provider;

import common.util.NetUtil;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import xyz.eevee.eevee.provider.model.JishoSearchResult;
import xyz.eevee.eevee.session.Session;

import java.io.IOException;
import java.util.Optional;

@Log4j2
public class JishoSearchProvider {
    /**
     * Given a search query return search results from jisho.org.
     *
     * @param query A string search query.
     * @return An optional JishoSearchResult representing the list of search results. If an error occurs
     * while fetching the result data then an empty Optional will be returned.
     */
    public static Optional<JishoSearchResult> getSearchResult(@NonNull String query) {
        try {
            String json = NetUtil.getPage(
                String.format(
                    Session.getSession().getConfiguration().readString("eevee.jishoSearchApiURL"),
                    query
                )
            );
            return Optional.of(
                Session.getSession().getObjectMapper().readValue(json, JishoSearchResult.class)
            );
        } catch (IOException e) {
            e.printStackTrace();
            log.error(String.format("Failed to search Jisho for query: %s.", query), e);
        }

        return Optional.empty();
    }
}
