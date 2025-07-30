package com.VegaSolutions.lpptransit.lppapi.data

import com.google.gson.annotations.SerializedName

data class ArrivalOnRoute(
    @SerializedName("name")
    val name: String,

    @SerializedName("station_code")
    val codeId: Int,

    @SerializedName("order_no")
    val orderNo: Int,

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("station_int_id")
    val stationIntId: Int,

    @SerializedName("arrivals")
    val arrivals: List<Arrival>,
) {
    data class Arrival(
        @SerializedName("route_id")
        val routeId: String,

        @SerializedName("vehicle_id")
        val vehicleId: String,

        @SerializedName("type")
        val type: Int,

        @SerializedName("eta_min")
        val etaMin: Int,

        @SerializedName("route_name")
        val routeName: String,

        @SerializedName("trip_name")
        val tripName: String,

        @SerializedName("depot")
        val depot: Int,
    )
}
