package com.VegaSolutions.lpptransit.routing;


import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.ArrayList;
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
        List<LatLng> latLngs;

        public String getType() {
            return type;
        }

        public List<double[]> getCoordinates() {
            return coordinates;
        }
        public List<LatLng> getLatLngList() {
            if (latLngs == null) {
                latLngs = new ArrayList<>();
                for (double[] coordinate : coordinates)
                    latLngs.add(new LatLng(coordinate[1], coordinate[0]));
                return latLngs;
            }
            return latLngs;
        }
    }



    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
