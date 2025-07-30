package com.VegaSolutions.lpptransit.data.travana

import android.content.Context

object SharedPreferences {
    const val ROUTE_FAVOURITES: String = "route_favourites"
    const val STATION_FAVOURITES: String = "station_favourites"

    fun getFavouriteRoutes(context: Context): Map<String, Boolean?> {
        val sharedPreferences =
            context.applicationContext.getSharedPreferences(ROUTE_FAVOURITES, Context.MODE_PRIVATE)
        return sharedPreferences.all as Map<String, Boolean?>
    }

    fun getFavouriteStations(context: Context): Map<String, Boolean?> {
        val sharedPreferences = context.applicationContext.getSharedPreferences(
            STATION_FAVOURITES,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.all as Map<String, Boolean?>
    }
}
