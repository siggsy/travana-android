package com.VegaSolutions.lpptransit.lppapi.responseobjects;

import java.util.List;

public class DepartureWrapper {

    private String station_code;
    private String station_name;
    private String route_destination;
    private String route_full_name;
    private List<Departure> departures;

    public String getStation_code() {
        return station_code;
    }

    public String getStation_name() {
        return station_name;
    }

    public String getRoute_destination() {
        return route_destination;
    }

    public String getRoute_full_name() {
        return route_full_name;
    }

    public List<Departure> getDepartures() {
        return departures;
    }

    public class Departure {
        private int arrival_hour;
        private int arrival_minute;

        public int getArrival_hour() {
            return arrival_hour;
        }

        public int getArrival_minute() {
            return arrival_minute;
        }
    }

}
