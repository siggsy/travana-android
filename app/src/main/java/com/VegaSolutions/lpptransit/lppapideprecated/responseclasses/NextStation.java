package com.VegaSolutions.lpptransit.lppapideprecated.responseclasses;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class NextStation {

    private int int_id;
    private String name;
    private int vehicle_int_id;
    private double longitude;
    private double latitude;
    private List<Connection> connections;

    /**
     * @return Station ID.
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
     * @return Station's geographical longitude.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * @return Station's geographical latitude.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @return Bus ID.
     */
    public int getVehicle_int_id() {
        return vehicle_int_id;
    }

    public LatLng getLatLng() { return new LatLng(latitude, longitude); }

    public static class Connection {

        private int vehicle_int_id;
        private String route_number;
        private String route_name;
        private int eta;
        private int validity;

        public int getVehicle_int_id() {
            return vehicle_int_id;
        }

        public String getRoute_number() {
            return route_number;
        }

        public String getRoute_name() {
            return route_name;
        }

        public int getEta() {
            return eta;
        }

        public int getValidity() {
            return validity;
        }
    }

}
