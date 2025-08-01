package com.VegaSolutions.lpptransit.data

import android.content.Context
import com.VegaSolutions.lpptransit.data.lppapi.NeoApi
import com.VegaSolutions.lpptransit.data.lppapi.data.StationArrivals
import com.VegaSolutions.lpptransit.data.lppapi.data.Route
import com.VegaSolutions.lpptransit.data.lppapi.data.Station
import com.VegaSolutions.lpptransit.data.travana.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

private val UPDATE_INTERVAL: Duration = 1.minutes


// TODO: Implement subscription counter on your own. Maybe ditch SharedFlow altogether.

class TravanaRepository(
    private val context: Context,
    private val api: NeoApi,
    private val scope: CoroutineScope,
) {
    private val stations = HashMap<String, MutableSharedFlow<StationArrivals>>()

    init {
        scope.launch {
            while(true) {
                updateStationArrivals()
                delay(UPDATE_INTERVAL)
            }
        }
    }

    private suspend fun updateStationArrivals() {
        stations.entries.removeIf { (_, v) -> v.subscriptionCount.value > 0 }
        stations.forEach { (k, v) ->
            v.tryEmit(api.arrival(k))
        }
    }

    suspend fun stationFlow(stationId: String): SharedFlow<StationArrivals> {
        val flow = stations.get(stationId)
        if (flow != null) return flow

        // SharedFlow with initial value
        val mutableFlow = MutableSharedFlow<StationArrivals>(replay = 1)
        mutableFlow.emit(api.arrival(stationId))

        stations[stationId] = mutableFlow
        return mutableFlow
    }

    suspend fun favouriteStations(): List<Station> {
        val stations = api.stationDetails(showSubroutes = true)
        val favouriteList = SharedPreferences.getFavouriteStations(context)
        return stations.filter { favouriteList[it.refId] ?: false }
    }

    suspend fun favouriteRoutes(): List<Route> {
        val routes = api.routes()
        val favList = SharedPreferences.getFavouriteRoutes(context)
        return routes.filter { favList[it.routeId] ?: false }
    }
}