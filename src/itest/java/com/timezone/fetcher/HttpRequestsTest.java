package com.timezone.fetcher;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.timezone.fetcher.handler.TimeHandlerResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ratpack.core.http.client.ReceivedResponse;
import ratpack.test.MainClassApplicationUnderTest;

import java.net.URLEncoder;
import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End to End integration tests.
 * Test -> Ratpack -> TImezoneDB
 */
@ExtendWith(MockitoExtension.class)
public class HttpRequestsTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    MainClassApplicationUnderTest appUnderTest = new MainClassApplicationUnderTest(Main.class);

    @Test
    public void validRequest() throws Exception {

        appUnderTest.test(testHttpClient -> {
            ReceivedResponse response = testHttpClient.get(buildUri("RO", "Bucharest"));

            TimeHandlerResponse parsedResponse = objectMapper.readValue(response.getBody().getBytes(), TimeHandlerResponse.class);
            assertThat(parsedResponse).isNotNull();
            assertThat(parsedResponse.getStatus()).isEqualTo(TimeHandlerResponse.Status.OK);
            assertThat(parsedResponse.getPayload().size()).isEqualTo(2);
        });

    }


    @Test
    public void noCity() throws Exception {

        appUnderTest.test(testHttpClient -> {
            ReceivedResponse response = testHttpClient.get(buildUri("RO", ""));

            TimeHandlerResponse parsedResponse = objectMapper.readValue(response.getBody().getBytes(), TimeHandlerResponse.class);
            assertThat(parsedResponse).isNotNull();
            assertThat(parsedResponse.getStatus()).isEqualTo(TimeHandlerResponse.Status.ERROR);
            assertThat(parsedResponse.getError()).contains("Missing city field");
        });
    }


    @Test
    public void sameCityMultipleTimes() throws Exception {

        appUnderTest.test(testHttpClient -> {
            ReceivedResponse response = testHttpClient.get(buildUri("US", "Nashville"));

            TimeHandlerResponse parsedResponse = objectMapper.readValue(response.getBody().getBytes(), TimeHandlerResponse.class);
            assertThat(parsedResponse).isNotNull();
            assertThat(parsedResponse.getStatus()).isEqualTo(TimeHandlerResponse.Status.OK);
            assertThat(parsedResponse.getPayload().size()).isEqualTo(8);
        });

    }

    @Test
    public void multipleMatches() throws Exception {

        appUnderTest.test(testHttpClient -> {
            ReceivedResponse response = testHttpClient.get(buildUri("US", "Timis*"));

            TimeHandlerResponse parsedResponse = objectMapper.readValue(response.getBody().getBytes(), TimeHandlerResponse.class);
            assertThat(parsedResponse).isNotNull();
            assertThat(parsedResponse.getStatus()).isEqualTo(TimeHandlerResponse.Status.OK);
            assertThat(parsedResponse.getPayload().size()).isEqualTo(4);
        });

    }

    @Test
    public void noCityMatch() throws Exception {

        appUnderTest.test(testHttpClient -> {
            ReceivedResponse response = testHttpClient.get(buildUri("RO", "TestQwe"));

            TimeHandlerResponse parsedResponse = objectMapper.readValue(response.getBody().getBytes(), TimeHandlerResponse.class);
            assertThat(parsedResponse).isNotNull();
            assertThat(parsedResponse.getStatus()).isEqualTo(TimeHandlerResponse.Status.ERROR);
            assertThat(parsedResponse.getError()).contains("Record not found");
        });
    }

    @Test
    public void shortWildcard() throws Exception {

        appUnderTest.test(testHttpClient -> {
            ReceivedResponse response = testHttpClient.get(buildUri("RO", "Te*"));

            TimeHandlerResponse parsedResponse = objectMapper.readValue(response.getBody().getBytes(), TimeHandlerResponse.class);
            assertThat(parsedResponse).isNotNull();
            assertThat(parsedResponse.getStatus()).isEqualTo(TimeHandlerResponse.Status.ERROR);
            assertThat(parsedResponse.getError()).contains("Minimum 5 characters needed for wildcard search");
        });
    }


    @Test
    public void notExistingCountry() throws Exception {

        appUnderTest.test(testHttpClient -> {
            ReceivedResponse response = testHttpClient.get(buildUri("TE", "Chicago"));

            TimeHandlerResponse parsedResponse = objectMapper.readValue(response.getBody().getBytes(), TimeHandlerResponse.class);
            assertThat(parsedResponse).isNotNull();
            assertThat(parsedResponse.getStatus()).isEqualTo(TimeHandlerResponse.Status.ERROR);
            assertThat(parsedResponse.getError()).contains("Invalid ISO 3166-1 country code");
        });
    }


    @AfterEach
    public void shutdown() {
        appUnderTest.close();
    }

    private String buildUri(String country, String city) {
        return String.format("/time?country=%s&city=%s", URLEncoder.encode(country, Charset.defaultCharset()), URLEncoder.encode(city, Charset.defaultCharset()));
    }

}
