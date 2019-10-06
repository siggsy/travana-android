package com.VegaSolutions.lpptransit.lppapideprecated.responseclasses;

import java.util.List;

public class LiveBusArrivalV2 {

    private List<Route> routes;

    public List<Route> getRoutes() {
        return routes;
    }

    public class Route {

        private List<Arrival> arrivals;
        private String route_group_number;
        private String route_parent_name;
        private String route_number;
        private String route_name;

        public List<Arrival> getArrivals() {
            return arrivals;
        }

        public String getRoute_group_number() {
            return route_group_number;
        }

        public String getRoute_parent_name() {
            return route_parent_name;
        }

        public String getRoute_number() {
            return route_number;
        }

        public String getRoute_name() {
            return route_name;
        }
    }

    public class Arrival {

        private int eta;
        private int validity;
        private String utc_timestamp;
        private String local_timestamp;
        private int route_int_id;
        private String route_specific_name;
        private boolean is_detour;
        private boolean is_garage;

        public int getEta() {
            return eta;
        }

        public int getValidity() {
            return validity;
        }

        public String getUtc_timestamp() {
            return utc_timestamp;
        }

        public String getLocal_timestamp() {
            return local_timestamp;
        }

        public int getRoute_int_id() {
            return route_int_id;
        }

        public String getRoute_specific_name() {
            return route_specific_name;
        }

        public boolean isIs_detour() {
            return is_detour;
        }

        public boolean isIs_garage() {
            return is_garage;
        }
        
    }

}
