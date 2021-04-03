package com.VegaSolutions.lpptransit.lppapi.responseobjects;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

public class BusOnRoute {

    @SerializedName("route_number")
    private String routeNumber;

    @SerializedName("route_id")
    private String routeId;

    @SerializedName("trip_id")
    private String tripId;

    @SerializedName("route_name")
    private String routeName;

    private String destination;

    @SerializedName("bus_unit_id")
    private String busUnitId;

    @SerializedName("bus_name")
    private String busName;

    @SerializedName("bus_timestamp")
    private String busTimestamp;

    private double longitude;
    private double latitude;
    private int altitude;

    @SerializedName("ground_speed")
    private float groundSpeed;

    @SerializedName("cardinal_direction")
    private float cardinalDirection;

    public String getRouteNumber() {
        return routeNumber;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getTripId() {
        return tripId;
    }

    public String getRouteName() {
        return routeName;
    }

    public String getDestination() {
        return destination;
    }

    public String getBusUnitId() {
        return busUnitId;
    }

    public String getBusName() {
        return busName;
    }

    public String getBusTimestamp() {
        return busTimestamp;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public int getAltitude() {
        return altitude;
    }

    public float getGroundSpeed() {
        return groundSpeed;
    }

    public float getCardinalDirection() {
        return cardinalDirection;
    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

}
