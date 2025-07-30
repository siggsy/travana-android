package com.VegaSolutions.lpptransit.lppapi.data

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

data class BusOnRoute(
    @SerializedName("route_number")
    val routeNumber: String,

    @SerializedName("route_id")
    val routeId: String,

    @SerializedName("trip_id")
    val tripId: String,

    @SerializedName("route_name")
    val routeName: String,

    @SerializedName("destination")
    val destination: String,

    @SerializedName("bus_unit_id")
    val busUnitId: String,

    @SerializedName("bus_name")
    val busName: String,

    @SerializedName("bus_timestamp")
    val busTimestamp: String,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("altitude")
    val altitude: Int,

    @SerializedName("ground_speed")
    val groundSpeed: Float,

    @SerializedName("cardinal_direction")
    val cardinalDirection: Float,
) {
    val latLng: LatLng
        get() = LatLng(latitude, longitude)
}
