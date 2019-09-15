package com.VegaSolutions.lpptransit.lppapi.responseobjects;

public class LiveBusArrival {

    private int station_int_id;
    private int route_int_id;
    private int vehicle_int_id;
    private int route_number;
    private String route_name;
    private int eta;
    private int validity;
    private String utc_timestamp;
    private String local_timestamp;

    /**
     * @return Station ID.
     */
    public int getStation_int_id() {
        return station_int_id;
    }

    /**
     * @return Route ID.
     */
    public int getRoute_int_id() {
        return route_int_id;
    }

    /**
     * @return Vehicle ID.
     */
    public int getVehicle_int_id() {
        return vehicle_int_id;
    }

    /**
     * @return Route number.
     */
    public int getRoute_number() {
        return route_number;
    }

    /**
     * @return Route name.
     */
    public String getRoute_name() {
        return route_name;
    }

    /**
     * @return Time till arrival in minutes.
     */
    public int getEta() {
        return eta;
    }

    /**
     * @return Time to data validity expiration in seconds.
     */
    public int getValidity() {
        return validity;
    }

    /**
     * @return UTC time.
     */
    public String getUtc_timestamp() {
        return utc_timestamp;
    }

    /**
     * @return Local time.
     */
    public String getLocal_timestamp() {
        return local_timestamp;
    }

}
