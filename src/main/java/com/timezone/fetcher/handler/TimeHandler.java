package com.timezone.fetcher.handler;

import com.timezone.fetcher.service.TimeZoneDbClient;
import com.timezone.fetcher.service.TimeZoneDbResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ratpack.core.handling.Context;
import ratpack.core.handling.Handler;
import ratpack.core.http.client.HttpClient;
import ratpack.core.jackson.Jackson;
import ratpack.exec.Promise;
import ratpack.func.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Handler of time requests:
 * - validate requests
 * - call invoke TimeZoneDbClient
 * - return result
 *
 */
@Slf4j
@RequiredArgsConstructor
public class TimeHandler implements Handler {

    private final TimeZoneDbClient timeZoneDbClient;

    @Override
    public void handle(Context ctx) throws Exception {
        log.info("Handling request");
        MultiValueMap<String, String> queryParams = ctx.getRequest().getQueryParams();
        String country = queryParams.get("country");
        String city = queryParams.get("city");

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
        promise.onError(e -> error(ctx, e.getMessage()));
        promise.then(r -> {

            if (!r.getStatus().equalsIgnoreCase(TimeZoneDbClient.STATUS_OK)) {
                error(ctx, r.getMessage());
                return;
            }

            List<TimeHandlerResponse.TimeAndTimezonePayload> response = new ArrayList<>();
            r.getZones().forEach(zone -> response.add(
                    TimeHandlerResponse.TimeAndTimezonePayload.builder()
                            .time(zone.getFormatted())
                            .zoneName(zone.getZoneName())
                            .build()
            ));

            ctx.render(Jackson.json(
                    TimeHandlerResponse.builder()
                            .status(TimeHandlerResponse.Status.OK)
                            .payload(response)
                            .build()
            ));
        });
    }

    private void error(Context ctx, String message) {
        ctx.render(Jackson.json(
                TimeHandlerResponse.builder()
                        .status(TimeHandlerResponse.Status.ERROR)
                        .error(message)
                        .build()
        ));
    }

}
