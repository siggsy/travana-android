package com.VegaSolutions.lpptransit.lppapideprecated.responseclasses;

public class AllStations {

    private int int_id;
    private String ref_id;
    private String name;
    private Geometry geometry;

    /**
     * @return Station identificator as integer.
     */
    public int getInt_id() {
        return int_id;
    }

    /**
     * @return Reference id of station.
     */
    public String getRef_id() {
        return ref_id;
    }

    /**
     * @return Station name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return GeoJSON object containing "coordinates" property with geographical latitude and longitude.
     */
    public Geometry getGeometry() {
        return geometry;
    }

}
