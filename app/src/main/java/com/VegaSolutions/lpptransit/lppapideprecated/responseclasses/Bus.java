package com.VegaSolutions.lpptransit.lppapideprecated.responseclasses;

public class Bus {

    private int int_id;
    private String reg_number;
    private int driver_int_id;
    private Geometry geometry;
    private int station_int_id;
    private int route_int_id;
    private String utc_timestamp;
    private String local_timestamp;
    private int direction;
    private String unix_timestamp;
    private int speed;


    public int getInt_id() {
        return int_id;
    }

    public String getReg_number() {
        return reg_number;
    }

    public int getDriver_int_id() {
        return driver_int_id;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public int getStation_int_id() {
        return station_int_id;
    }

    public int getRoute_int_id() {
        return route_int_id;
    }

    public String getUtc_timestamp() {
        return utc_timestamp;
    }

    public String getLocal_timestamp() {
        return local_timestamp;
    }

    public int getDirection() {
        return direction;
    }

    public String getUnix_timestamp() {
        return unix_timestamp;
    }

    public int getSpeed() {
        return speed;
    }

}
