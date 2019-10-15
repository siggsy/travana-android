package com.VegaSolutions.lpptransit.lppapi.responseobjects;

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
        private int ref_id;
        private String name;
        private int code_id;

        public int getRef_id() {
            return ref_id;
        }

        public String getName() {
            return name;
        }

        public int getCode_id() {
            return code_id;
        }
    }

    public class Arrival {
        private String route_id;
        private String trip_id;
        private String vehicle_id;
        private int type;
        private int eta_min;
        private String route_name;
        private String trip_name;
        private int depot;
        private Stations stations;

        public String getRoute_id() {
            return route_id;
        }

        public String getTrip_id() {
            return trip_id;
        }

        public String getVehicle_id() {
            return vehicle_id;
        }

        /**
         * A type of arrival (0 - predicted, 1 - scheduled, 2 - approaching station (prihod), 3 - detour (obvoz))
         * @return int type
         */
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

        /**
         * 0 if normal route, 1 if vehicle is headed to garage
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
