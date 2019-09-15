package com.VegaSolutions.lpptransit.lppapi.responseobjects;

public class RouteParents {

    private String id;
    private String route_group_id;
    private String name;

    /**
     * @return Name of the route parent.
     */
    public String getName() {
        return name;
    }

    /**
     * @return ID of the route parent.
     */
    public String getId() {
        return id;
    }

    /**
     * @return ID of the route group.
     */
    public String getRoute_group_id() {
        return route_group_id;
    }

}
