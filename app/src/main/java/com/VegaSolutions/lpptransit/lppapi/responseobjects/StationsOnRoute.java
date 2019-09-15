package com.VegaSolutions.lpptransit.lppapi.responseobjects;

public class StationsOnRoute {

    private String id;
    private String super_station_id;
    private int int_id;
    private String ref_id;
    private String name;
    private double longitude;
    private double latitude;
    private String route_id;
    private int route_int_id;
    private int order_no;
    private Geometry geometry;

    /**
     * @deprecated Use getGeometry() instead.
     * @return Station's latitude in decimal numerical form.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @deprecated Use getGeometry() instead.
     * @return Station's longitude in decimal numerical form.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * @return Station identification.
     */
    public String getId() {
        return id;
    }

    /**
     * @return Station name in Slovenian.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Numerical station ID.
     */
    public int getInt_id() {
        return int_id;
    }

    /**
     * @return Ref ID ??.
     */
    public String getRef_id() {
        return ref_id;
    }

    /**
     * @return Super station ID.
     */
    public String getSuper_station_id() {
        return super_station_id;
    }

    /**
     * @return GeoJSON objects containing coordinates of station.
     */
    public Geometry getGeometry() {
        return geometry;
    }

    /**
     * @return Presumably numerical order of station, currently not implemented in DB.
     */
    public int getOrder_no() {
        return order_no;
    }

    /**
     * @return Numerical ID of route for this station.
     */
    public int getRoute_int_id() {
        return route_int_id;
    }

    /**
     * @return Identification of route for this station.
     */
    public String getRoute_id() {
        return route_id;
    }
}
