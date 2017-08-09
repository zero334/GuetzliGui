package com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by zero334 on 08.08.2017.
 */
public class HttpRequest {

    String url;
    String charset = java.nio.charset.StandardCharsets.UTF_8.name();
    ArrayList<Pair<String, String>> query;

    public HttpRequest(final String url, final ArrayList<Pair<String, String>> query) {
        this.url = url;
        this.query = query;
    }

    public HttpRequest(final String url) {
        this.url = url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public void setCharset(final String charset) {
        this.charset = charset;
    }

    public void setQuery(final ArrayList<Pair<String, String>>  query) {
        this.query = query;
    }

    public String httpGet() {
        final String response = this.fireHttpGet(this.url, this.charset, this.query);
        return response;
    }

    private String fireHttpGet(final String url, final String charset, ArrayList<Pair<String, String>> query) {

        final String queryString = this.parseQuery(query);

        try {
            final URLConnection connection = new URL(url + queryString).openConnection();
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1");
            final InputStream response = connection.getInputStream();
            return this.responseHandling(response, connection.getHeaderField("Content-Type"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    private String responseHandling(InputStream response, final String contentType) {

        String responseCharset = null;
        for (final String param : contentType.replace(" ", "").split(";")) {
            if (param.startsWith("charset=")) {
                responseCharset = param.split("=", 2)[1];
                break;
            }
        }

        String parsedResponse = "";
        if (responseCharset != null) {
            try {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(response, responseCharset))) {
                    for (String line; (line = reader.readLine()) != null;) {
                        parsedResponse += line;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return parsedResponse;
    }

    private String parseQuery(ArrayList<Pair<String, String>> query) {
        if (query == null) {
            return "";
        }

        String queryString = "";
        for (final Pair<String, String> queryElements : query) {
            if (!queryElements.getFirst().isEmpty() && !queryElements.getSecond().isEmpty()) {
                final StringBuilder buildQueryString = new StringBuilder(queryElements.getFirst());
                buildQueryString.append('=');
                buildQueryString.append(queryElements.getSecond());
                buildQueryString.append('&');

                queryString += buildQueryString.toString();
            }
        }

        // Cut last & char
        if ((queryString.length() > 0) && (queryString.charAt(queryString.length() - 1) == '&')) {
            queryString = queryString.substring(0, queryString.length() - 1);
        }

        return '?' + queryString;
    }

}

class Pair<L,R> {

    private final L left;
    private final R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L getFirst() {
        return left;
    }

    public R getSecond() {
        return right;
    }
}