package com.VegaSolutions.lpptransit.routing;


import java.util.List;

public class RouteResponse {

    String code;
    List<Route> routes;

    public String getCode() {
        return code;
    }

    public List<Route> getRoutes() {
        return routes;
    }
}
