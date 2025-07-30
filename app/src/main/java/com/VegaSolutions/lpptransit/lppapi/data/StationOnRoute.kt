package com.VegaSolutions.lpptransit.lppapi.data

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

open class StationOnRoute(
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
) {
    val latLng: LatLng
        get() = LatLng(latitude, longitude)
}
