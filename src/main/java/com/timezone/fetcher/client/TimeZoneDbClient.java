package com.timezone.fetcher.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import ratpack.core.http.client.HttpClient;
import ratpack.core.http.client.ReceivedResponse;
import ratpack.exec.Promise;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * Client to TimezoneDB service - perform basic request and deserialization of responses.
 */
public class TimeZoneDbClient {

    public static final String STATUS_OK = "OK";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final String apiEndpoint;

    private final String apiKey;

    public TimeZoneDbClient(String apiKey) {
        this("api.timezonedb.com", apiKey);
    }

    public TimeZoneDbClient(String apiEndpoint, String apiKey) {
        this.apiEndpoint = apiEndpoint;
        this.apiKey = apiKey;
    }


    /**
     * Fetch Timezone of City.
     * Reference: https://timezonedb.com/references/get-time-zone
     */
    public Promise<TimeZoneDbResponse> fetchTimezones(HttpClient httpClient, String country, String city) throws Exception {
        String uri = String.format("http://%s/v2.1/get-time-zone?key=%s&by=city&country=%s&city=%s&format=json",
                apiEndpoint,
                apiKey,
                URLEncoder.encode(country, Charset.defaultCharset()),
                URLEncoder.encode(city, Charset.defaultCharset()));

        return httpClient.get(new URI(uri)).map(response -> {
            return parseTimezone(response);
        });
    }

    TimeZoneDbResponse parseTimezone(ReceivedResponse response) throws IOException {
        return objectMapper.readValue(response.getBody().getBytes(), TimeZoneDbResponse.class);
    }

}
