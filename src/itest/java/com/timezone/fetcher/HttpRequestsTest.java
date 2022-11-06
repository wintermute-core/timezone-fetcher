package com.timezone.fetcher;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ratpack.core.http.client.ReceivedResponse;
import ratpack.test.MainClassApplicationUnderTest;

@ExtendWith(MockitoExtension.class)
public class HttpRequestsTest {

    MainClassApplicationUnderTest appUnderTest = new MainClassApplicationUnderTest(Main.class);

    @Test
    public void validRequest() throws Exception {

        appUnderTest.test(testHttpClient -> {
            ReceivedResponse response = testHttpClient.get("/time");
        });

    }

    @AfterEach
    public void shutdown() {
        appUnderTest.close();
    }

    private String buildUri(String country, String city) {
        return String.format("/time?country=%s&city=%s", country, city);
    }

}
