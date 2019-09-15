package com.VegaSolutions.lpptransit.lppapi.responseobjects;

public class RouteGroups {

    private String name;
    private String id;

    /**
     * @return Name of the route group (LPP route number on bus, ex. 6B, 1, 19I...).
     */
    public String getName() {
        return name;
    }

    /**
     * @return Unique ID of route group.
     */
    public String getId() {
        return id;
    }

}
