package common.util;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;

public class NetUtil {
    /**
     * Performs a GET request and returns a page's contents as a string.
     *
     * @param url The URL of the page to fetch.
     * @return The contents of the fetched page as a string.
     * @throws IOException Thrown in the event that the request fails.
     */
    public static String getPage(@NonNull String url) throws IOException {
       return getPage(url, null);
    }

    /**
     * Performs a GET request and returns a page's contents as a string.
     *
     * @param url The URL of the page to fetch.
     * @param headers Additional request headers to set when making the request.
     * @return The contents of the fetched page as a string.
     * @throws IOException Thrown in the event that the request fails.
     */
    public static String getPage(@NonNull String url, @NonNull HttpHeaders headers) throws IOException {
        HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
        HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(url));

        if (headers != null) {
            request.setHeaders(headers);
        }

        return request.execute().parseAsString();
    }

    /**
     * Performs a GET request and returns a page's contents as an input stream. This is useful if you want
     * to redirect the contents of the page elsewhere.
     *
     * @param url The URL of the page to fetch.
     * @return The contents of the fetched page as a stream that can be read.
     * @throws IOException Thrown in the event that the request fails.
     */
    public static InputStream streamPage(@NonNull String url) throws IOException {
        HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
        HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(url));
        return request.execute().getContent();
    }
}
