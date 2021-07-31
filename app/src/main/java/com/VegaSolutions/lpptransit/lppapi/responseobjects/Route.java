package com.VegaSolutions.lpptransit.lppapi.responseobjects;

import android.graphics.Color;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.data.Geometry;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonGeometryCollection;
import com.google.maps.android.data.geojson.GeoJsonLineString;
import com.google.maps.android.data.geojson.GeoJsonLineStringStyle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Route {

    @SerializedName("trip_id")
    private String tripId;

    @SerializedName("route_id")
    private String routeId;

    @SerializedName("route_number")
    private String routeNumber;

    @SerializedName("route_name")
    private String routeName;

    @SerializedName("short_route_name")
    private String shortRouteName;

    @SerializedName("trip_int_id")
    private String tripIntId;

    @SerializedName("geojson_shape")
    private JsonObject geoJSON;

    public String getTripId() {
        return tripId;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getRouteNumber() {
        return routeNumber;
    }

    public String getRouteName() {
        return routeName;
    }

    public String getRouteShortName() {
        return shortRouteName;
    }

    public String getShortRouteName() {
        return shortRouteName;
    }

    public String getTripIntId() {
        return tripIntId;
    }

    public JSONObject getGeoJSON() {
        try {
            return new JSONObject(geoJSON.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return "Route{" +
                "tripId='" + tripId + '\'' +
                ", routeId='" + routeId + '\'' +
                ", routeNumber='" + routeNumber + '\'' +
                ", routeName='" + routeName + '\'' +
                ", shortRouteName='" + shortRouteName + '\'' +
                ", tripIntId='" + tripIntId + '\'' +
                '}';
    }
}
