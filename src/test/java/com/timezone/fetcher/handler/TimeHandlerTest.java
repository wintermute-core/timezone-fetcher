package com.timezone.fetcher.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timezone.fetcher.client.TimeZoneDbClient;
import com.timezone.fetcher.client.TimeZoneDbResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ratpack.core.http.client.HttpClient;
import ratpack.core.http.client.ReceivedResponse;
import ratpack.exec.Promise;
import ratpack.test.mock.MockApi;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TimeHandlerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private TimeZoneDbClient timeZoneDbClient;

    private MockApi mockApi;

    @BeforeEach
    void setup() {
        mockApi = MockApi.of(request -> new TimeHandler(timeZoneDbClient));
    }

    @Test
    public void validRequest() throws Exception {
        when(timeZoneDbClient.fetchTimezones(any(HttpClient.class), eq("RO"), eq("Bucharest"))).thenReturn(
                Promise.value(TimeZoneDbResponse.builder().status(TimeZoneDbClient.STATUS_OK).zones(
                        Collections.singletonList(TimeZoneDbResponse.TimeZones.builder().build())
                ).build())
        );
        mockApi.test(httpClient -> {
            ReceivedResponse response = httpClient.get(buildUri("RO", "Bucharest"));
            TimeHandlerResponse parsedResponse = objectMapper.readValue(response.getBody().getBytes(), TimeHandlerResponse.class);
            assertThat(parsedResponse).isNotNull();
            assertThat(parsedResponse.getStatus()).isEqualTo(TimeHandlerResponse.Status.OK);
            assertThat(parsedResponse.getPayload().size()).isEqualTo(1);
        });
    }

    @Test
    public void emptyData() throws Exception {
        mockApi.test(httpClient -> {
            // empty country
            ReceivedResponse response = httpClient.get(buildUri("", "Bucharest"));
            TimeHandlerResponse parsedResponse = objectMapper.readValue(response.getBody().getBytes(), TimeHandlerResponse.class);
            assertThat(parsedResponse).isNotNull();
            assertThat(parsedResponse.getStatus()).isEqualTo(TimeHandlerResponse.Status.ERROR);
            assertThat(parsedResponse.getError()).contains("country");

            // empty city
            response = httpClient.get(buildUri("Romania", ""));
            parsedResponse = objectMapper.readValue(response.getBody().getBytes(), TimeHandlerResponse.class);
            assertThat(parsedResponse).isNotNull();
            assertThat(parsedResponse.getStatus()).isEqualTo(TimeHandlerResponse.Status.ERROR);
            assertThat(parsedResponse.getError()).contains("city");
        });
    }

    private String buildUri(String country, String city) {
        return String.format("/time?country=%s&city=%s", country, city);
    }

}
