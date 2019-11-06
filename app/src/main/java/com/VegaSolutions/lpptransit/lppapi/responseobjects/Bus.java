package com.VegaSolutions.lpptransit.lppapi.responseobjects;

import com.google.android.gms.maps.model.LatLng;

public class Bus {

    private String bus_unit_id;
    private String name;
    private String vin;
    private String timestamp;

    private double coordinate_x;
    private double coordinate_y;
    private double coordinate_z;

    private float cardinal_direction;
    private float ground_speed;
    private boolean ignition_value;
    private boolean engine_value;

    private String driver_id;
    private int odo;

    public String getBus_unit_id() {
        return bus_unit_id;
    }

    public String getName() {
        return name;
    }

    public String getVin() {
        return vin;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public double getCoordinate_x() {
        return coordinate_x;
    }

    public double getCoordinate_y() {
        return coordinate_y;
    }

    public double getCoordinate_z() {
        return coordinate_z;
    }

    public float getCardinal_direction() {
        return cardinal_direction;
    }

    public float getGround_speed() {
        return ground_speed;
    }

    public boolean isIgnition_value() {
        return ignition_value;
    }

    public boolean isEngine_value() {
        return engine_value;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public int getOdo() {
        return odo;
    }

    public LatLng getLatLng() {
        return new LatLng(coordinate_y, coordinate_x);
    }
}
