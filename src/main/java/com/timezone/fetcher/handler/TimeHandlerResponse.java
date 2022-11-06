package com.timezone.fetcher.handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

/**
 * API response model classes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeHandlerResponse {

    public enum Status { OK, ERROR }

    private Status status;

    private String error;

    private Collection<TimeAndTimezonePayload> payload;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeAndTimezonePayload {
        String zoneName;
        String time;
    }


}
