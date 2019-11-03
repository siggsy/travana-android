package com.VegaSolutions.lpptransit.lppapi.responseobjects;

import com.google.android.gms.maps.model.LatLng;

public class BusOnRoute {

    private String route_number;
    private String route_id;
    private String trip_id;
    private String route_name;
    private String destination;
    private String bus_unit_id;
    private String bus_name;
    private String bus_timestamp;
    private double longitude;
    private double latitude;
    private int altitude;
    private float ground_speed;
    private float cardinal_direction;

    public String getRoute_number() {
        return route_number;
    }

    public String getRoute_id() {
        return route_id;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public String getRoute_name() {
        return route_name;
    }

    public String getDestination() {
        return destination;
    }

    public String getBus_unit_id() {
        return bus_unit_id;
    }

    public String getBus_name() {
        return bus_name;
    }

    public String getBus_timestamp() {
        return bus_timestamp;
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

    public float getGround_speed() {
        return ground_speed;
    }

    public float getCardinal_direction() {
        return cardinal_direction;
    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

}
