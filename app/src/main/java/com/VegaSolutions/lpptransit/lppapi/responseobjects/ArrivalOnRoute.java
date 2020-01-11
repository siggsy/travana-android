package com.VegaSolutions.lpptransit.lppapi.responseobjects;

import java.util.List;

public class ArrivalOnRoute extends StationOnRoute {

    List<Arrival> arrivals;

    public List<Arrival> getArrivals() {
        return arrivals;
    }

    public class Arrival {

        private String route_id;
        private String vehicle_id;
        private int type;
        private int eta_min;
        private String route_name;
        private String trip_name;
        private int depot;

        public String getRoute_id() {
            return route_id;
        }

        public String getVehicle_id() {
            return vehicle_id;
        }

        public int getType() {
            return type;
        }

        public int getEta_min() {
            return eta_min;
        }

        public String getRoute_name() {
            return route_name;
        }

        public String getTrip_name() {
            return trip_name;
        }

        public int getDepot() {
            return depot;
        }

    }

}
