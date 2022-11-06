package com.timezone.fetcher.service;

import lombok.Data;

import java.util.Collection;

@Data
public class TimeZoneDbResponse {

    private String status;
    private String message;

    private int totalPage;
    private int currentPage;

    private Collection<TimeZones> zones;

    @Data
    public static class TimeZones {
        private String countryCode;
        private String countryName;
        private String regionName;
        private String cityName;
        private String zoneName;
        private String abbreviation;
        private int gmtOffset;
        private int dst;
        private long zoneStart;
        private long timestamp;
        private String formatted;
        private long zoneEnd;
        private String nextAbbreviation;
    }

}
