package com.VegaSolutions.lpptransit.lppapi.responseobjects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TimetableWrapper {

    private Station station;

    @SerializedName("route_groups")
    private List<RouteGroup> routeGroups;

    public Station getStation() {
        return station;
    }

    public List<RouteGroup> getRouteGroups() {
        return routeGroups;
    }

    public class Station {

        @SerializedName("ref_id")
        private String refId;

        private String name;

        public String getRefId() {
            return refId;
        }

        public String getName() {
            return name;
        }
    }

    public class RouteGroup {

        @SerializedName("route_group_number")
        private String routeGroupNumber;

        private List<Route> routes;

        public String getRouteGroupNumber() {
            return routeGroupNumber;
        }

        public List<Route> getRoutes() {
            return routes;
        }

        public class Route {

            private List<Timetable> timetable;
            private List<Station> stations;
            private String name;

            @SerializedName("parent_name")
            private String parentName;

            @SerializedName("group_name")
            private String groupName;

            @SerializedName("route_number_suffix")
            private String routeNumberSuffix;

            @SerializedName("route_number_prefix")
            private String routeNumberPrefix;

            public List<Timetable> getTimetable() {
                return timetable;
            }

            public List<Station> getStations() {
                return stations;
            }

            public String getName() {
                return name;
            }

            public String getParentName() {
                return parentName;
            }

            public String getGroupName() {
                return groupName;
            }

            public String getRouteNumberSuffix() {
                return routeNumberSuffix;
            }

            public String getRouteNumberPrefix() {
                return routeNumberPrefix;
            }

            public class Timetable {
                private int hour;
                private int[] minutes;

                @SerializedName("is_current")
                private boolean isCurrent;

                public int getHour() {
                    return hour;
                }

                public int[] getMinutes() {
                    return minutes;
                }

                public boolean isCurrent() {
                    return isCurrent;
                }
            }

            public class Station {

                @SerializedName("ref_id")
                private String refId;

                private String name;

                @SerializedName("order_no")
                private int orderNo;

                public String getRefId() {
                    return refId;
                }

                public String getName() {
                    return name;
                }

                public int getOrderNo() {
                    return orderNo;
                }
            }
        }

    }

}
