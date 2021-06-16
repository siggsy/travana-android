package com.VegaSolutions.lpptransit.lppapi.responseobjects;

import com.google.gson.annotations.SerializedName;

public class Route {

    @SerializedName("trip_id")
    private String tripId;

    @SerializedName("route_id")
    private String routeId;

    @SerializedName("route_number")
    private String routeNumber;

    @SerializedName("route_name")
    private String routeName;

    @SerializedName("short_route_name")
    private String shortRouteName;

    @SerializedName("trip_int_id")
    private String tripIntId;

    public String getTripId() {
        return tripId;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getRouteNumber() {
        return routeNumber;
    }

    public String getRouteName() {
        return routeName;
    }

    public String getRouteShortName() {
        return shortRouteName;
    }

    public String getShortRouteName() {
        return shortRouteName;
    }

    public String getTripIntId() {
        return tripIntId;
    }

    @Override
    public String toString() {
        return "Route{" +
                "tripId='" + tripId + '\'' +
                ", routeId='" + routeId + '\'' +
                ", routeNumber='" + routeNumber + '\'' +
                ", routeName='" + routeName + '\'' +
                ", shortRouteName='" + shortRouteName + '\'' +
                ", tripIntId='" + tripIntId + '\'' +
                '}';
    }
}
