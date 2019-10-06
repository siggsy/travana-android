package com.VegaSolutions.lpptransit.lppapi.responseobjects;

import java.util.List;

public class Station {

    private String name;
    private int int_id;
    private double latitude;
    private double longitude;
    private String ref_id;
    private List<String> route_groups_on_station;

    public String getName() {
        return name;
    }

    public int getInt_id() {
        return int_id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getRef_id() {
        return ref_id;
    }

    public List<String> getRoute_groups_on_station() {
        return route_groups_on_station;
    }
}
