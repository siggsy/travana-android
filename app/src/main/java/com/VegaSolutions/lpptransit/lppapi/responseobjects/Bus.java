package com.VegaSolutions.lpptransit.lppapi.responseobjects;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

public class Bus {

    @SerializedName("bus_unit_id")
    private String busUnitId;

    private String name;
    private String vin;
    private String timestamp;

    @SerializedName("coordinate_x")
    private double coordinateX;

    @SerializedName("coordinate_y")
    private double coordinateY;

    @SerializedName("coordinate_z")
    private double coordinateZ;

    @SerializedName("cardinal_direction")
    private float cardinalDirection;

    @SerializedName("ground_speed")
    private float groundSpeed;

    @SerializedName("ignition_value")
    private boolean ignitionValue;

    @SerializedName("engine_value")
    private boolean engineValue;

    private String driverId;
    private int odo;

    public String getBusUnitId() {
        return busUnitId;
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

    public double getCoordinateX() {
        return coordinateX;
    }

    public double getCoordinateY() {
        return coordinateY;
    }

    public double getCoordinateZ() {
        return coordinateZ;
    }

    public float getCardinalDirection() {
        return cardinalDirection;
    }

    public float getGroundSpeed() {
        return groundSpeed;
    }

    public boolean isIgnitionValue() {
        return ignitionValue;
    }

    public boolean isEngineValue() {
        return engineValue;
    }

    public String getDriverId() {
        return driverId;
    }

    public int getOdo() {
        return odo;
    }

    public LatLng getLatLng() {
        return new LatLng(coordinateY, coordinateX);
    }
}
