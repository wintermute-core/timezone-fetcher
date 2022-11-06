package com.timezone.fetcher.service;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ratpack.core.http.TypedData;
import ratpack.core.http.client.ReceivedResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TimeZoneDbClientTest {

    @Mock
    private ReceivedResponse receivedResponse;

    @Mock
    private TypedData typedData;

    @Test
    public void responseDeserialization() throws Exception {
        when(receivedResponse.getBody()).thenReturn(typedData);
        when(typedData.getBytes()).thenReturn(
                IOUtils.toByteArray(this.getClass().getClassLoader().getResourceAsStream("timezonedb.json"))
        );
        TimeZoneDbClient timeZoneDbClient = new TimeZoneDbClient("test");
        TimeZoneDbResponse response = timeZoneDbClient.parseTimezone(receivedResponse);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("OK");
        assertThat(response.getZones().size()).isEqualTo(4);

        TimeZoneDbResponse.TimeZones timeZones = response.getZones().iterator().next();
        assertThat(timeZones.getFormatted()).isEqualTo("2022-11-06 18:13:35");
        assertThat(timeZones.getZoneName()).isEqualTo("Europe/Bucharest");
    }

    @Test
    public void errorHandling() throws Exception {
        when(receivedResponse.getBody()).thenReturn(typedData);
        when(typedData.getBytes()).thenReturn(
                IOUtils.toByteArray(this.getClass().getClassLoader().getResourceAsStream("timezonedb-error.json"))
        );
        TimeZoneDbClient timeZoneDbClient = new TimeZoneDbClient("test");
        TimeZoneDbResponse response = timeZoneDbClient.parseTimezone(receivedResponse);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("FAILED");
        assertThat(response.getZones().size()).isEqualTo(0);
    }

}
