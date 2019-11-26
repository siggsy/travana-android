package com.VegaSolutions.lpptransit.lppapi.responseobjects;

import java.util.List;

public class TimetableWrapper {

    private Station station;
    private List<RouteGroup> route_groups;

    public Station getStation() {
        return station;
    }

    public List<RouteGroup> getRoute_groups() {
        return route_groups;
    }

    public class Station {
        private String ref_id;
        private String name;

        public String getRef_id() {
            return ref_id;
        }

        public String getName() {
            return name;
        }
    }

    public class RouteGroup {
        private String route_group_number;
        private List<Route> routes;

        public String getRoute_group_number() {
            return route_group_number;
        }

        public List<Route> getRoutes() {
            return routes;
        }

        public class Route {
            private List<Timetable> timetable;
            private List<Station> stations;
            private String name;
            private String parent_name;
            private String group_name;
            private String route_number_suffix;
            private String route_number_prefix;

            public List<Timetable> getTimetable() {
                return timetable;
            }

            public List<Station> getStations() {
                return stations;
            }

            public String getName() {
                return name;
            }

            public String getParent_name() {
                return parent_name;
            }

            public String getGroup_name() {
                return group_name;
            }

            public String getRoute_number_suffix() {
                return route_number_suffix;
            }

            public String getRoute_number_prefix() {
                return route_number_prefix;
            }

            public class Timetable {
                private int hour;
                private int[] minutes;
                private boolean is_current;

                public int getHour() {
                    return hour;
                }

                public int[] getMinutes() {
                    return minutes;
                }

                public boolean isCurrent() {
                    return is_current;
                }
            }

            public class Station {
                private String ref_id;
                private String name;
                private int order_no;

                public String getRef_id() {
                    return ref_id;
                }

                public String getName() {
                    return name;
                }

                public int getOrder_no() {
                    return order_no;
                }
            }
        }

    }

}
