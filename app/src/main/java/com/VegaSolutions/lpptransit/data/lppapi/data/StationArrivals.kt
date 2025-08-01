package com.VegaSolutions.lpptransit.data.lppapi.data

import com.google.gson.annotations.SerializedName

data class StationArrivals(
    @SerializedName("station")
    val station: Station,

    @SerializedName("arrivals")
    val arrivals: List<Arrival>
) {
    data class Station(
        @SerializedName("ref_id")
        val refId: Int,

        @SerializedName("name")
        val name: String,

        @SerializedName("code_id")
        val codeId: Int,
    ) {
        val towards: Boolean
            get() = refId.mod(2) == 0
    }

    data class Arrival(
        @SerializedName("route_id")
        val routeId: String,

        @SerializedName("trip_id")
        val tripId: String,

        @SerializedName("vehicle_id")
        val vehicleId: String,

        /**
         * A type of arrival (0 - predicted, 1 - scheduled, 2 - approaching station (prihod), 3 - detour (obvoz))
         */
        @SerializedName("type")
        val type: Int,

        @SerializedName("eta_min")
        val etaMin: Int,

        @SerializedName("route_name")
        val routeName: String,

        @SerializedName("trip_name")
        val tripName: String,

        /**
         * 0 if normal route, 1 if vehicle is headed to garage
         */
        @SerializedName("depot")
        val depot: Int,

        @SerializedName("stations")
        val stations: Stations,
    ) {
        data class Stations(
            @SerializedName("departure")
            val departure: String,

            @SerializedName("arrival")
            val arrival: String,
        )
    }
}
