package com.VegaSolutions.lpptransit.lppapi.responseobjects;

import com.google.gson.annotations.SerializedName;

public class RouteOnStation {

    @SerializedName("trip_id")
    private String tripId;

    @SerializedName("route_id")
    private String routeId;

    @SerializedName("route_number")
    private String routeNumber;

    @SerializedName("route_name")
    private String routeName;

    @SerializedName("route_group_name")
    private String routeGroupName;

    @SerializedName("is_garage")
    private boolean isGarage;


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

    public String getRouteGroupName() {
        return routeGroupName;
    }

    public boolean isGarage() {
        return isGarage;
    }
}
