package com.VegaSolutions.lpptransit.lppapideprecated.responseclasses;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class StationById {

    private List<String> route_groups_on_station;
    private int int_id;
    private String ref_id;
    private String name;
    private double latitude;
    private double longitude;

    /**
     * @return Station reference ID.
     */
    public String getRef_id() {
        return ref_id;
    }

    /**
     * @return Station name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Station ID.
     */
    public int getInt_id() {
        return int_id;
    }

    /**
     * @return Station's geographical latitude.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @return Station's geographical longitude.
     */
    public double getLongitude() {
        return longitude;
    }

    public LatLng getLatLng() { return new LatLng(latitude, longitude); }

    /**
     * @return All route groups on station.
     */
    public List<String> getRoute_groups_on_station() {
        return route_groups_on_station;
    }

}