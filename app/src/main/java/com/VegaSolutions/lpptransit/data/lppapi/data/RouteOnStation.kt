package com.VegaSolutions.lpptransit.data.lppapi.data

import com.google.gson.annotations.SerializedName

data class RouteOnStation(
    @SerializedName("trip_id")
    val tripId: String,

    @SerializedName("route_id")
    val routeId: String,

    @SerializedName("route_number")
    val routeNumber: String,

    @SerializedName("route_name")
    val routeName: String,

    @SerializedName("route_group_name")
    val routeGroupName: String,

    @SerializedName("is_garage")
    val isGarage: Boolean,
)
