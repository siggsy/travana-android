package com.VegaSolutions.lpptransit.lppapi.responseobjects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ArrivalOnRoute extends StationOnRoute {

    List<Arrival> arrivals;

    public List<Arrival> getArrivals() {
        return arrivals;
    }

    public class Arrival {

        @SerializedName("route_id")
        private String routeId;

        @SerializedName("vehicle_id")
        private String vehicleId;

        private int type;

        @SerializedName("eta_min")
        private int etaMin;

        @SerializedName("route_name")
        private String routeName;

        @SerializedName("trip_name")
        private String tripName;

        private int depot;

        public String getRouteId() {
            return routeId;
        }

        public String getVehicleId() {
            return vehicleId;
        }

        public int getType() {
            return type;
        }

        public int getEtaMin() {
            return etaMin;
        }

        public String getRouteName() {
            return routeName;
        }

        public String getTripName() {
            return tripName;
        }

        public int getDepot() {
            return depot;
        }

    }

}
