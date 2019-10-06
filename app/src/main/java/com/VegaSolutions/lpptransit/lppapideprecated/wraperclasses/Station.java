package com.VegaSolutions.lpptransit.lppapideprecated.wraperclasses;

import com.VegaSolutions.lpptransit.lppapideprecated.responseclasses.Geometry;
import com.VegaSolutions.lpptransit.lppapideprecated.responseclasses.StationsInRange;
import com.VegaSolutions.lpptransit.lppapideprecated.responseclasses.StationsOnRoute;

import java.util.ArrayList;
import java.util.List;

public class Station {

    // StationsInRange
    private String id;
    private String super_station_id;
    private int int_id;
    private String ref_id;
    private String name;
    private Geometry geometry;

    // StationsOnRoute - StationsInRange
    private String route_id;
    private int route_int_id;
    private int order_no;

    // StationById - Geometry
    private List<String> route_groups_on_station;

    public String getId() {
        return id;
    }

    public String getSuper_station_id() {
        return super_station_id;
    }

    public int getInt_id() {
        return int_id;
    }

    public String getRef_id() {
        return ref_id;
    }

    public String getName() {
        return name;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public String getRoute_id() {
        return route_id;
    }

    public int getRoute_int_id() {
        return route_int_id;
    }

    public int getOrder_no() {
        return order_no;
    }

    public List<String> getRoute_groups_on_station() {
        return route_groups_on_station;
    }

    public static Station getInstanceFromStationsInRange(StationsInRange stationInRange) {
        return new Station(
                stationInRange.getId(),
                stationInRange.getSuper_station_id(),
                stationInRange.getInt_id(),
                stationInRange.getRef_id(),
                stationInRange.getName(),
                stationInRange.getGeometry(),
                null,
                -1,
                -1,
                null
        );
    }
    public static List<Station> getInstancesFromStationsInRange(List<StationsInRange> stationsInRange) {
        List<Station> stations = new ArrayList<>();
        for (StationsInRange station : stationsInRange)
            stations.add(getInstanceFromStationsInRange(station));
        return stations;
    }

    public static Station getInstanceFromStationsOnRoute(StationsOnRoute stationOnRoute) {
        return new Station(
                stationOnRoute.getId(),
                stationOnRoute.getSuper_station_id(),
                stationOnRoute.getInt_id(),
                stationOnRoute.getRef_id(),
                stationOnRoute.getName(),
                stationOnRoute.getGeometry(),
                stationOnRoute.getRoute_id(),
                stationOnRoute.getRoute_int_id(),
                stationOnRoute.getOrder_no(),
                null);
    }
    public static List<Station> getInstancesFromStationsOnRoute(List<StationsOnRoute> stationsOnRoute) {
        List<Station> stations = new ArrayList<>();
        for (StationsOnRoute station : stationsOnRoute)
            stations.add(getInstanceFromStationsOnRoute(station));
        return stations;
    }

    private Station(String id, String super_station_id, int int_id, String ref_id, String name, Geometry geometry, String route_id, int route_int_id, int order_no, List<String> route_groups_on_station) {

        this.id = id;
        this.super_station_id = super_station_id;
        this.int_id = int_id;
        this.ref_id = ref_id;
        this.name = name;
        this.geometry = geometry;
        this.route_id = route_id;
        this.route_int_id = route_int_id;
        this.order_no = order_no;
        this.route_groups_on_station = route_groups_on_station;

    }

}
