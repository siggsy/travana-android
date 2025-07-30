package com.VegaSolutions.lpptransit.lppapi.data

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import org.json.JSONException
import org.json.JSONObject

data class Route(
    @SerializedName("trip_id")
    val tripId: String,

    @SerializedName("route_id")
    val routeId: String,

    @SerializedName("route_number")
    val routeNumber: String,

    @SerializedName("route_name")
    val routeName: String,

    @SerializedName("short_route_name")
    val routeShortName: String,

    @SerializedName("trip_int_id")
    val tripIntId: String,

    @SerializedName("geojson_shape")
    private val geoJSON: JsonObject?,
) {
    fun getGeoJSON(): JSONObject? {
        try {
            return JSONObject(geoJSON.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }
}
