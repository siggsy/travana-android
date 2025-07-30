package com.VegaSolutions.lpptransit.data.lppapi.data

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

data class Bus(
    @SerializedName("bus_unit_id")
    val busUnitId: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("vin")
    val vin: String,

    @SerializedName("timestamp")
    val timestamp: String,

    @SerializedName("coordinate_x")
    val coordinateX: Double,

    @SerializedName("coordinate_y")
    val coordinateY: Double,

    @SerializedName("coordinate_z")
    val coordinateZ: Double,

    @SerializedName("cardinal_direction")
    val cardinalDirection: Float,

    @SerializedName("ground_speed")
    val groundSpeed: Float,

    @SerializedName("ignition_value")
    val isIgnitionValue: Boolean,

    @SerializedName("engine_value")
    val isEngineValue: Boolean,

    @SerializedName("driver_id")
    val driverId: String,

    @SerializedName("odo")
    val odo: Int,
) {
    val latLng: LatLng
        get() = LatLng(coordinateY, coordinateX)
}
