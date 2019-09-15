package com.VegaSolutions.lpptransit.lppapi.responseobjects;

public class Geometry {

    private String type;
    private double[] coordinates;

    /**
     * @return The type of the geometry.
     */
    public String getType() {
        return type;
    }

    /**
     * @return The coordinates.
     * @apiNote (0 = longitude, 1 = latitude)
     */
    public double[] getCoordinates() {
        return coordinates;
    }

}
