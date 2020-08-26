package com.VegaSolutions.lpptransit.lppapi.responseobjects;

public class Route {

    private String trip_id;
    private String route_id;
    private String route_number;
    private String route_name;
    private String short_route_name;
    private String trip_int_id;

    public String getTrip_id() {
        return trip_id;
    }

    public String getRoute_id() {
        return route_id;
    }

    public String getRoute_number() {
        return route_number;
    }

    public String getRoute_name() {
        return route_name;
    }

    public String getRoute_short_name() {
        return short_route_name;
    }

    public String getShort_route_name() {
        return short_route_name;
    }

    public String getTrip_int_id() {
        return trip_int_id;
    }

    @Override
    public String toString() {
        return "Route{" +
                "trip_id='" + trip_id + '\'' +
                ", route_id='" + route_id + '\'' +
                ", route_number='" + route_number + '\'' +
                ", route_name='" + route_name + '\'' +
                ", short_route_name='" + short_route_name + '\'' +
                ", trip_int_id='" + trip_int_id + '\'' +
                '}';
    }
}
