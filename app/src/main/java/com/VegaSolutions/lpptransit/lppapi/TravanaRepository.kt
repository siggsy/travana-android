package com.VegaSolutions.lpptransit.lppapi

import android.content.Context
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Station
import com.VegaSolutions.lpptransit.utility.LppHelper

class TravanaRepository(
    private val context: Context,
    private val api: NeoApi
) {

    suspend fun favouriteStations(): List<Station> {
        val stations = api.stationDetails(showSubroutes = true)
        val favouriteList = LppHelper.getFavouriteStations(context)
        return stations.filter { favouriteList[it.refId] ?: false }
    }

}