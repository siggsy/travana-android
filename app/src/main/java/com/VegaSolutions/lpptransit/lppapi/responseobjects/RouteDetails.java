package com.VegaSolutions.lpptransit.lppapi.responseobjects;

public class RouteDetails {

    private String route_parent_id;
    private int int_id;
    private int opposite_route_int_id;
    private String name;
    private double length;

    /**
     * @return ID of route's parent route.
     */
    public String getRoute_parent_id() {
        return route_parent_id;
    }

    /**
     * @return Route integer identification.
     */
    public int getInt_id() {
        return int_id;
    }

    /**
     * @return Integer ID of opposite route.
     */
    public int getOpposite_route_int_id() {
        return opposite_route_int_id;
    }

    /**
     * @return Route name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Length of route in meters.
     */
    public double getLength() {
        return length;
    }

}
