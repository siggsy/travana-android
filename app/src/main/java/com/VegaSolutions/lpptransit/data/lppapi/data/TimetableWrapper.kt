package com.VegaSolutions.lpptransit.data.lppapi.data

import com.google.gson.annotations.SerializedName

data class TimetableWrapper(
    @SerializedName("station")
    val station: Station,

    @SerializedName("route_groups")
    val routeGroups: List<RouteGroup>,
) {
    data class Station(
        @SerializedName("ref_id")
        val refId: String,

        @SerializedName("name")
        val name: String,
    )

    data class RouteGroup(
        @SerializedName("route_group_number")
        val routeGroupNumber: String,

        @SerializedName("routes")
        val routes: List<Route>,
    ) {
        data class Route(
            @SerializedName("timetable")
            val timetable: List<Timetable>,

            @SerializedName("stations")
            val stations: List<Station>,

            @SerializedName("name")
            val name: String,

            @SerializedName("parent_name")
            val parentName: String,

            @SerializedName("group_name")
            val groupName: String,

            @SerializedName("route_number_suffix")
            val routeNumberSuffix: String,

            @SerializedName("route_number_prefix")
            val routeNumberPrefix: String,

            @SerializedName("is_garage")
            val isGarage: Boolean,
        ) {
            data class Timetable(
                @SerializedName("hour")
                val hour: Int,

                @SerializedName("minutes")
                val minutes: List<Int>,

                @SerializedName("timestamp")
                val timestamp: String,

                @SerializedName("is_current")
                val isCurrent: Boolean,
            )

            data class Station(
                @SerializedName("ref_id")
                val refId: String,

                @SerializedName("name")
                val name: String,

                @SerializedName("order_no")
                val orderNo: Int,
            )
        }
    }
}
