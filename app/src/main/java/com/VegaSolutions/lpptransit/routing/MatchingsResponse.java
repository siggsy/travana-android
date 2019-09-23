package com.VegaSolutions.lpptransit.routing;

import java.util.List;

public class MatchingsResponse {

    public String code;
    public List<Route> matchings;

    public static class Matching {

        public float confidence;
        public Route.Geometry geometry;

    }

}

