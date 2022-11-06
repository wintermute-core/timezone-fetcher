package com.timezone.fetcher.handler;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TimeHandlerResponse<Payload> {

    public enum Status { OK, ERROR }

    private Status status;

    private String error;

    private Payload payload;

    @Data
    @Builder
    public static class TimeAndTimezonePayload {
        private String zoneName;
        private String time;
    }


}
