package com.timezone.fetcher.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timezone.fetcher.service.TimeZoneDbClient;
import com.timezone.fetcher.service.TimeZoneDbResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ratpack.core.handling.Context;
import ratpack.core.handling.Handler;
import ratpack.core.http.client.HttpClient;
import ratpack.exec.Promise;

@Slf4j
@RequiredArgsConstructor
public class TimeHandler implements Handler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final TimeZoneDbClient timeZoneDbClient;

    @Override
    public void handle(Context ctx) throws Exception {
        log.info("Handling request");
        String country = ctx.getRequest().getQueryParams().get("country");
        String city = ctx.getRequest().getQueryParams().get("city");

        if (StringUtils.isBlank(country)) {
            error(ctx, "Missing country field");
            return;
        }
        if (StringUtils.isBlank(city)) {
            error(ctx, "Missing city field");
            return;
        }
        HttpClient client = ctx.get(HttpClient.class);
        Promise<TimeZoneDbResponse> promise = timeZoneDbClient.fetchTimezones(client, country, city);
        promise.then(r -> {
            byte[] data = objectMapper.writeValueAsBytes(
                    TimeHandlerResponse.builder()
                            .status(TimeHandlerResponse.Status.OK)
                            .payload(r)
                            .build()
            );
            ctx.getResponse().send(data);
        });
    }

    private void error(Context ctx, String message) throws JsonProcessingException {
        ctx.getResponse().send(
                objectMapper.writeValueAsBytes(
                        TimeHandlerResponse.builder()
                                .status(TimeHandlerResponse.Status.ERROR)
                                .error(message)
                                .build()

                )
        );
    }

}
