package com.VegaSolutions.lpptransit.lppapi.responseobjects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ArrivalWrapper {

    private Station station;
    private List<Arrival> arrivals;

    public Station getStation() {
        return station;
    }

    public List<Arrival> getArrivals() {
        return arrivals;
    }

    public class Station {

        @SerializedName("ref_id")
        private int refId;

        private String name;

        @SerializedName("code_id")
        private int codeId;

        public int getRefId() {
            return refId;
        }

        public String getName() {
            return name;
        }

        public int getCodeId() {
            return codeId;
        }
    }

    public class Arrival {

        @SerializedName("route_id")
        private String routeId;

        @SerializedName("trip_id")
        private String tripId;

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
        private Stations stations;

        public String getRouteId() {
            return routeId;
        }

        public String getTripId() {
            return tripId;
        }

        public String getVehicleId() {
            return vehicleId;
        }

        /**
         * A type of arrival (0 - predicted, 1 - scheduled, 2 - approaching station (prihod), 3 - detour (obvoz))
         *
         * @return int type
         */
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

        /**
         * 0 if normal route, 1 if vehicle is headed to garage
         *
         * @return int depot
         */
        public int getDepot() {
            return depot;
        }

        public Stations getStations() {
            return stations;
        }

        public class Stations {
            private String departure;
            private String arrival;

            public String getDeparture() {
                return departure;
            }

            public String getArrival() {
                return arrival;
            }
        }
    }

}
