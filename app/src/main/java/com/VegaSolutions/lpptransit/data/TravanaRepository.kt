package com.VegaSolutions.lpptransit.data

import android.content.Context
import com.VegaSolutions.lpptransit.data.lppapi.NeoApi
import com.VegaSolutions.lpptransit.data.lppapi.data.Station
import com.VegaSolutions.lpptransit.data.travana.SharedPreferences

class TravanaRepository(
    private val context: Context,
    private val api: NeoApi
) {
    suspend fun favouriteStations(): List<Station> {
        val stations = api.stationDetails(showSubroutes = true)
        val favouriteList = SharedPreferences.getFavouriteStations(context)
        return stations.filter { favouriteList[it.refId] ?: false }
    }
}