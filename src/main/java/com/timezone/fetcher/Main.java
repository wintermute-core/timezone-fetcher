package com.timezone.fetcher;

import com.timezone.fetcher.handler.TimeHandler;
import com.timezone.fetcher.service.TimeZoneDbClient;
import ratpack.core.server.RatpackServer;

import java.io.File;

/**
 * Application entry point
 */
public class Main {

    public static void main(String... args) throws Exception {

        String endpoint = System.getenv().getOrDefault("TIMEZONE_DB_ENDPOINT", "vip.timezonedb.com");
        String key = System.getenv().getOrDefault("TIMEZONE_DB_KEY", "3IEQDGPTOLJY");

        TimeZoneDbClient zoneDbClient = new TimeZoneDbClient(endpoint, key);
        RatpackServer.start(server -> server
                .serverConfig(config -> config.baseDir(new File("html").getAbsoluteFile()))
                .handlers(chain ->
                        chain.files(f -> f.dir("").indexFiles("index.html")).
                        get("time", ctx -> ctx.insert(new TimeHandler(zoneDbClient))))
        );
    }

}
