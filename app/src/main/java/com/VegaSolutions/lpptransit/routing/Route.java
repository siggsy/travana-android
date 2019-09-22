package com.VegaSolutions.lpptransit.routing;


import com.google.gson.Gson;

import java.util.List;

public class Route {

    float distance;
    float duration;
    Geometry geometry;

    public float getDistance() {
        return distance;
    }

    public float getDuration() {
        return duration;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public static class Geometry {

        String type;
        List<double[]> coordinates;

        public String getType() {
            return type;
        }

        public List<double[]> getCoordinates() {
            return coordinates;
        }
    }



    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
