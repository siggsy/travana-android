package com.VegaSolutions.lpptransit.lppapi.responseobjects;

public class StationOnRoute {

    private String name;
    private int code_id;
    private int order_no;
    private double latitude;
    private double longitude;

    public String getName() {
        return name;
    }

    public int getCode_id() {
        return code_id;
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
}
