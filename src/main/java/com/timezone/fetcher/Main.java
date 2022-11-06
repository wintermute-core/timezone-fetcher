package com.timezone.fetcher;

import com.timezone.fetcher.handler.TimeHandler;
import com.timezone.fetcher.service.TimeZoneDbClient;
import ratpack.core.server.RatpackServer;

import java.io.File;

public class Main {

    public static void main(String... args) throws Exception {
        TimeZoneDbClient zoneDbClient = new TimeZoneDbClient("vip.timezonedb.com", "3IEQDGPTOLJY");
        RatpackServer.start(server -> server
                .serverConfig(config -> {
                    config.baseDir(new File("html").getAbsoluteFile());
                })

                .handlers(chain ->
                        chain.files(f -> f.dir("").indexFiles("index.html")).
                        get("time", ctx -> ctx.insert(new TimeHandler(zoneDbClient))))
        );
    }

}
