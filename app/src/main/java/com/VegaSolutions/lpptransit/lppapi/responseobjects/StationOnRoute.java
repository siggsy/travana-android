package com.VegaSolutions.lpptransit.lppapi.responseobjects;

import com.google.android.gms.maps.model.LatLng;

public class StationOnRoute {

    private String name;
    private int station_code;
    private int order_no;
    private double latitude;
    private double longitude;

    public String getName() {
        return name;
    }

    public int getCode_id() {
        return station_code;
    }

    public int getOrder_no() {
        return order_no;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }
}
