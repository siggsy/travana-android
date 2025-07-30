package com.VegaSolutions.lpptransit.lppapi.data

import com.google.gson.annotations.SerializedName

data class DepartureWrapper(
    @SerializedName("station_code")
    val stationCode: String,

    @SerializedName("station_name")
    val stationName: String,

    @SerializedName("route_destination")
    val routeDestination: String,

    @SerializedName("route_full_name")
    val routeFullName: String,

    @SerializedName("departures")
    val departures: List<Departure>,
) {
    data class Departure (
        @SerializedName("arrival_hour")
        val arrivalHour: Int,

        @SerializedName("arrival_minute")
        val arrivalMinute: Int,
    )
}
