package com.VegaSolutions.lpptransit.lppapi.responseobjects;

public class Routes {

    private String id;
    private String route_parent_id;
    private int int_id;
    private int opposite_route_int_id;
    private String group_name;
    private double length;
    private String parent_name;
    private String route_name;

    /**
     * @return Unique ID of this route.
     */
    public String getId() {
        return id;
    }

    /**
     * @return ID of parent route within this route group.
     */
    public String getRoute_parent_id() {
        return route_parent_id;
    }

    /**
     * @return Integer ID of this route.
     */
    public int getInt_id() {
        return int_id;
    }

    /**
     * @return Integer ID of opposite route, can be null.
     */
    public int getOpposite_route_int_id() {
        return opposite_route_int_id;
    }

    /**
     * @return Name of bus group route.
     */
    public String getGroup_name() {
        return group_name;
    }

    /**
     * @return Floating point length of route in meters.
     */
    public double getLength() {
        return length;
    }

    /**
     * @return Name of parent route within this route group.
     */
    public String getParent_name() {
        return parent_name;
    }

    /**
     * @return Name of the route.
     */
    public String getRoute_name() {
        return route_name;
    }

}
