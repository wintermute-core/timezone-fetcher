package com.timezone.fetcher;

import com.timezone.fetcher.handler.TimeHandler;
import com.timezone.fetcher.service.TimeZoneDbClient;
import ratpack.core.server.RatpackServer;

public class Main {

    public static void main(String... args) throws Exception {

        TimeZoneDbClient zoneDbClient = new TimeZoneDbClient("vip.timezonedb.com", "3IEQDGPTOLJY");

        RatpackServer.start(server -> server
                .handlers(chain -> {
                    chain.get("time", ctx -> {
                        ctx.insert(new TimeHandler(zoneDbClient));
                    });

                })
        );
    }

}
