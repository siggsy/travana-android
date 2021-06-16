package com.VegaSolutions.lpptransit.lppapi.responseobjects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DepartureWrapper {

    @SerializedName("station_code")
    private String stationCode;

    @SerializedName("station_name")
    private String stationName;

    @SerializedName("route_destination")
    private String routeDestination;

    @SerializedName("route_full_name")
    private String routeFullName;

    private List<Departure> departures;

    public String getStationCode() {
        return stationCode;
    }

    public String getStationName() {
        return stationName;
    }

    public String getRouteDestination() {
        return routeDestination;
    }

    public String getRouteFullName() {
        return routeFullName;
    }

    public List<Departure> getDepartures() {
        return departures;
    }

    public class Departure {

        @SerializedName("arrival_hour")
        private int arrivalHour;

        @SerializedName("arrival_minute")
        private int arrivalMinute;

        public int getArrivalHour() {
            return arrivalHour;
        }

        public int getArrivalMinute() {
            return arrivalMinute;
        }
    }

}
