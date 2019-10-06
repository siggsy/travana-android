package com.VegaSolutions.lpptransit.lppapideprecated.responseclasses;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.GsonBuilder;

public class StationsInRange {

    private String id;
    private String super_station_id;
    private int int_id;
    private String ref_id;
    private String name;
    private double latitude;
    private double longitude;
    private Geometry geometry;

    /**
     * @return Integer ID of station.
     */
    public int getInt_id() {
        return int_id;
    }

    /**
     * @return Station name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return GeoJSON objects containing coordinates of station.
     */
    public Geometry getGeometry() {
        return geometry;
    }

    /**
     * @return ID of station.
     */
    public String getId() {
        return id;
    }

    /**
     * @return Reference id of station.
     */
    public String getRef_id() {
        return ref_id;
    }

    /**
     * @return ID of super station, currently unused.
     */
    public String getSuper_station_id() {
        return super_station_id;
    }

    /**
     * @deprecated Use getGeometry() instead.
     * @return Double longitude of the station.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * @deprecated Use getGeometry() instead.
     * @return Double latitude of the station.
     */
    public double getLatitude() {
        return latitude;
    }

    public LatLng getLatLng() { return new LatLng(latitude, longitude); }

    @Override
    public String toString() {
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        return builder.create().toJson(this);
    }

}
