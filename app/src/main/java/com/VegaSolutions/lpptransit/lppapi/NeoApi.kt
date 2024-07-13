package com.VegaSolutions.lpptransit.lppapi

import android.content.Context
import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.VegaSolutions.lpptransit.BuildConfig
import com.VegaSolutions.lpptransit.lppapi.responseobjects.ApiResponse
import com.VegaSolutions.lpptransit.lppapi.responseobjects.ArrivalOnRoute
import com.VegaSolutions.lpptransit.lppapi.responseobjects.ArrivalWrapper
import com.VegaSolutions.lpptransit.lppapi.responseobjects.BusOnRoute
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Route
import com.VegaSolutions.lpptransit.lppapi.responseobjects.RouteOnStation
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Station
import com.VegaSolutions.lpptransit.lppapi.responseobjects.TimetableWrapper
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.parameters
import io.ktor.serialization.gson.gson
import okhttp3.Cache
import java.io.File
import java.util.concurrent.TimeUnit

const val DATA_URL = "https://data.lpp.si/api"
const val DETOUR_URL = "https://www.lpp.si"

// Bus
const val BUS_DETAILS = "/bus/bus-details"
const val BUSES_ON_ROUTE = "/bus/buses-on-route"
const val DRIVER = "/bus/driver"

// Route
const val ACTIVE_ROUTES = "/route/active-routes"
const val ROUTES = "/route/routes"
const val STATIONS_ON_ROUTE = "/route/stations-on-route"
const val ARRIVALS_ON_ROUTE = "/route/arrivals-on-route"

// Station
const val ARRIVAL = "/station/arrival"
const val ROUTES_ON_STATION = "/station/routes-on-station"
const val STATION_DETAILS = "/station/station-details"
const val TIMETABLE = "/station/timetable"
const val MESSAGES = "/station/messages"

// Timetable
const val ROUTE_DEPARTURES = "/timetable/route-departures"

// Detours
const val DETOURS = "/javni-prevoz/obvozi/"

class NeoApi() {

    // --------------------------- [ OkHttp Client ] -------------------------------------------- //

    private val client = HttpClient(OkHttp) {

        // JSON parser
        install(ContentNegotiation) {
            gson()
        }

        // OkHttp client configuration
        engine {
            config {
                followRedirects(true)
                connectTimeout(7, TimeUnit.SECONDS)
                readTimeout(7, TimeUnit.SECONDS)
                addInterceptor(GzipInterceptor())
            }
        }
    }

    private suspend inline fun <reified T> HttpClient.lppGet(
        url: String,
        vararg params: Pair<String, String>
    ): T {
        val response = client.get(url) {
            headers {
                append("apikey",          BuildConfig.LPP_API_KEY)
                append("User-Agent",      "Travana/${BuildConfig.VERSION_CODE}")
                append("Accept",          "application/json")
                append("Accept-Encoding", "gzip")
                append("Cache-Control",   "no-cache")
            }
            parameters {
                params.forEach {
                    append(it.first, it.second)
                }
            }
        }
        if (response.status.value !in 200..299) {
            throw HttpError(response.status)
        }

        val result: ApiResponse<T> = response.body()
        if (!result.isSuccess) {
            throw ApiError(result.type, result.message)
        }
        return result.data
    }

    // --------------------------- [ Endpoints ] ------------------------------------------------ //

    private fun Boolean.toParam() = if (this) "1" else "0"

    suspend fun busesOnRoute(routeGroupNumber: String): List<BusOnRoute> =
        client.lppGet("$DATA_URL$BUSES_ON_ROUTE")

    suspend fun activeRoutes(): List<Route> =
        client.lppGet("$DATA_URL$ACTIVE_ROUTES")
    
    suspend fun routes(): List<Route> =
        client.lppGet("$DATA_URL$ROUTES")

    suspend fun routes(routeId: String, shape: Boolean = false): List<Route> =
        client.lppGet(
            "$DATA_URL$ROUTES",
            "route-id" to routeId,
            "shape" to shape.toParam()
        )

    suspend fun arrivalsOnRoute(tripId: String): List<ArrivalOnRoute> =
        client.lppGet(
            "$DATA_URL$ARRIVALS_ON_ROUTE",
            "trip-id" to tripId
        )

    suspend fun arrival(stationCode: String): ArrivalWrapper =
        client.lppGet(
            "$DATA_URL$ARRIVAL",
            "station-code" to stationCode
        )

    suspend fun routesOnStation(stationCode: String): List<RouteOnStation> =
        client.lppGet(
            "$DATA_URL$ROUTES_ON_STATION",
            "station-code" to stationCode
        )

    suspend fun stationDetails(stationCode: String, showSubroutes: Boolean): Station =
        client.lppGet(
            "$DATA_URL$STATION_DETAILS",
            "station-code" to stationCode,
            "show-subroutes" to showSubroutes.toParam()
        )

    suspend fun stationDetails(showSubroutes: Boolean): List<Station> =
        client.lppGet(
            "$DATA_URL$STATION_DETAILS",
            "show-subroutes" to showSubroutes.toParam()
        )

    suspend fun stationMessages(stationCode: String): List<String> =
        client.lppGet(
            "$DATA_URL$MESSAGES",
            "station-code" to stationCode
        )

    suspend fun timetable(
        stationCode: String,
        nextHours: Int,
        prevHours: Int,
        vararg routeGroupNumbers: Int
    ): TimetableWrapper =
        client.lppGet(
            "$DATA_URL$TIMETABLE",
            "station-code" to stationCode,
            "next-hours" to nextHours.toString(),
            "prev-hours" to prevHours.toString(),
            *(routeGroupNumbers.map { "route-group-number" to it.toString() }.toTypedArray())
        )

    // ------------------------------------------------------------------------------------------ //
}
