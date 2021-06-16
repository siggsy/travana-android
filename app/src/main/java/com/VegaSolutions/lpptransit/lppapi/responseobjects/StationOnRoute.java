package com.VegaSolutions.lpptransit.lppapi.responseobjects;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

public class StationOnRoute {

    private String name;

    @SerializedName("station_code")
    private int stationCode;

    @SerializedName("order_no")
    private int orderNo;

    private double latitude;
    private double longitude;

    @SerializedName("station_int_id")
    private int stationIntId;

    public String getName() {
        return name;
    }

    public int getCode_id() {
        return stationCode;
    }

    public int getOrderNo() {
        return orderNo;
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

    public int getStationCode() {
        return stationCode;
    }

    public int getStationIntId() {
        return stationIntId;
    }
}
