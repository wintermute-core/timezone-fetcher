package com.timezone.fetcher.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ratpack.core.http.client.HttpClient;
import ratpack.core.http.client.ReceivedResponse;
import ratpack.exec.Promise;

import java.io.IOException;
import java.net.URI;

/**
 * Client to TimezoneDB service.
 */
public class TimeZoneDbClient {

    private final String apiEndpoint;

    private final String apiKey;

    public TimeZoneDbClient(String apiKey) {
        this("api.timezonedb.com", apiKey);
    }

    public TimeZoneDbClient(String apiEndpoint, String apiKey) {
        this.apiEndpoint = apiEndpoint;
        this.apiKey = apiKey;
    }

    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Fetch Timezone of City.
     * Reference: https://timezonedb.com/references/get-time-zone
     */
    public Promise<TimeZoneDbResponse> fetchTimezones(HttpClient httpClient, String country, String city) throws Exception {
        String uri = String.format("http://%s/v2.1/get-time-zone?key=%s&by=city&country=%s&city=%s&format=json",
                apiEndpoint,
                apiKey,
                country,
                city);

        return httpClient.get(new URI(uri)).map(response -> {
            return parseTimezone(response);
        });
    }

    TimeZoneDbResponse parseTimezone(ReceivedResponse response) throws IOException {
        return objectMapper.readValue(response.getBody().getBytes(), TimeZoneDbResponse.class);
    }


}
