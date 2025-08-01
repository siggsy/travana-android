package com.VegaSolutions.lpptransit.neoui.screens.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.VegaSolutions.lpptransit.data.TravanaRepository
import com.VegaSolutions.lpptransit.neoui.components.Arrival
import com.VegaSolutions.lpptransit.neoui.components.Route
import com.VegaSolutions.lpptransit.neoui.components.Station
import com.VegaSolutions.lpptransit.utility.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: TravanaRepository) : ViewModel() {

    private val _stations = MutableLiveData<Resource<List<Station>>>()
    val stations: LiveData<Resource<List<Station>>> = _stations

    private val _arrivals = HashMap<String, MutableLiveData<Station>>()

    fun fetchFavouriteArrivals() = viewModelScope.launch(Dispatchers.IO) {
        val stations = repository.favouriteStations()
        val arrivals = repository.stationFlow(stations.first().refId)
        val routes = repository.favouriteRoutes().map { it.routeId }.toHashSet()

//        val res = arrivals.map {
//            Station(
//                id = it.station.refId.toString(),
//                name = it.station.name,
//                towards = it.station.towards,
//                routes = it.arrivals.distinctBy { it.routeId }.sortedBy { it.routeName }
////                    .filter { route -> routes.contains(route.routeId) }
//                    .map { route ->
//                        Route(
//                            number = route.routeName,
//                            name = route.stations.arrival,
//                            arrivals = it.arrivals
//                                .filter { arrival -> arrival.routeId == route.routeId }
//                                .map { arrival ->
//                                    Arrival(
//                                        isLive = arrival.type == 0 || arrival.type == 2,
//                                        isGarage = arrival.depot == 1,
//                                        eta = arrival.etaMin
//                                    )
//                                }
//                        )
//                    }
//            )
//        }
//        _stations.postValue(Resource.Success(res))
    }

}