package com.VegaSolutions.lpptransit.lppapideprecated;

import com.VegaSolutions.lpptransit.lppapideprecated.responseclasses.AllStations;
import com.VegaSolutions.lpptransit.lppapideprecated.responseclasses.ApiResponse;
import com.VegaSolutions.lpptransit.lppapideprecated.responseclasses.Bus;
import com.VegaSolutions.lpptransit.lppapideprecated.responseclasses.LiveBusArrival;
import com.VegaSolutions.lpptransit.lppapideprecated.responseclasses.LiveBusArrivalV2;
import com.VegaSolutions.lpptransit.lppapideprecated.responseclasses.NextStation;
import com.VegaSolutions.lpptransit.lppapideprecated.responseclasses.RouteDetails;
import com.VegaSolutions.lpptransit.lppapideprecated.responseclasses.RouteGroups;
import com.VegaSolutions.lpptransit.lppapideprecated.responseclasses.RouteParents;
import com.VegaSolutions.lpptransit.lppapideprecated.responseclasses.Routes;
import com.VegaSolutions.lpptransit.lppapideprecated.responseclasses.StationById;
import com.VegaSolutions.lpptransit.lppapideprecated.responseclasses.StationsInRange;
import com.VegaSolutions.lpptransit.lppapideprecated.responseclasses.StationsOnRoute;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Lpp Api wrapper class
 */
public class Api {

    /**
     * Returns information about single route identified by station's ID or int_id.
     * @param route_int_id int ID of route
     * @param callback callback to be triggered when finished
     */
    public static void getRouteDetails(int route_int_id, ApiCallback<RouteDetails> callback) {
        new LppQuery()
                .addParams("route_int_id", String.valueOf(route_int_id))
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<RouteDetails> data = new Gson().fromJson(response, new TypeToken<ApiResponse<RouteDetails>>(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else
                        callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.GET_ROUTE_DETAILS);
    }

    /**
     * Returns list of LPP Route Groups.
     * @param callback callback to be triggered when finished
     */
    public static void getRouteGroups(ApiCallback<RouteGroups> callback) {
        new LppQuery()
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<RouteGroups> data = new Gson().fromJson(response, new TypeToken<ApiResponse<RouteGroups>>(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else
                        callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.GET_ROUTE_GROUPS);
    }

    /**
     * Returns a list of LPP bus routes in specified route group.
     * @param route_id Route group ID. If not present, server will check for presence of route_name parameter.
     * @param callback callback to be triggered when finished
     */
    public static void getRoutes_route_id(String route_id, ApiCallback<List<Routes>> callback) {
        new LppQuery()
                .addParams("route_id", route_id)
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<Routes>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<Routes>>>(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else
                        callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.GET_ROUTES);
    }

    /**
     * Returns a list of LPP bus routes in specified route group.
     * @param route_name Route group name, ex. 6, 19, 1 ...
     * @param callback callback to be triggered when finished
     */
    public static void getRoutes_route_name(String route_name, ApiCallback<List<Routes>> callback) {
        new LppQuery()
                .addParams("route_name", route_name)
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<Routes>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<Routes>>>(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else
                        callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.GET_ROUTES);
    }

    public static void getRoutes(ApiCallback<List<Routes>> callback) {
        new LppQuery()
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<Routes>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<Routes>>>(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else
                        callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.GET_ROUTES);
    }

    /**
     * Returns data for stations on particular route.
     * @param route_id ID of route (can be acquired with /api/getRoutes)
     * @param callback callback to be triggered when finished
     */
    public static void getStationsOnRoute(String route_id, ApiCallback<List<StationsOnRoute>> callback) {
        new LppQuery()
                .addParams("route_id", route_id)
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<StationsOnRoute>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<StationsOnRoute>> >(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else
                        callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.GET_STATIONS_ON_ROUTE);
    }

    /**
     * Returns data for stations on particular route.
     * @param route_int_id ID of route (can be acquired with /api/getRoutes)
     * @param callback callback to be triggered when finished
     */
    public static void getStationsOnRoute(int route_int_id, ApiCallback<List<StationsOnRoute>> callback) {
        new LppQuery()
                .addParams("route_int_id", String.valueOf(route_int_id))
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<StationsOnRoute>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<StationsOnRoute>> >(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else
                        callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.GET_STATIONS_ON_ROUTE);
    }

    /**
     * Returns data for parent stations.
     * @param route_int_id ID of route (can be acquired with /api/getRoutes)
     * @param callback callback to be triggered when finished
     */
    public static void getRouteParents(int route_int_id, ApiCallback<List<RouteParents>> callback) {
        new LppQuery()
                .addParams("route_int_id", String.valueOf(route_int_id))
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<RouteParents>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<RouteParents>>>(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else
                        callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.GET_ROUTE_PARENTS);
    }

    /**
     * Returns data for parent stations.
     * @param route_id ID of route (can be acquired with /api/getRoutes)
     * @param callback callback to be triggered when finished
     */
    public static void getRouteParents(String route_id, ApiCallback<List<RouteParents>> callback) {
        new LppQuery()
                .addParams("route_id", route_id)
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<RouteParents>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<RouteParents>>>(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else
                        callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.GET_ROUTE_PARENTS);
    }

    /**
     * Returns a list of bus routes traveling over particular station.
     * @param station_id ID of station.
     * @param callback callback to be triggered when finished
     */
    public static void getRoutesOnStation(String station_id, ApiCallback<List<String>> callback) {
        new LppQuery()
                .addParams("station_id", station_id)
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<String>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<String>>>(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else
                        callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.GET_ROUTES_ON_STATION);
    }

    /**
     * Returns a list of bus routes traveling over particular station.
     * @param station_int_id Integer ID of station.
     * @param callback callback to be triggered when finished.
     */
    public static void getRoutesOnStation(int station_int_id, ApiCallback<List<String>> callback) {
        new LppQuery()
                .addParams("station_int_id", String.valueOf(station_int_id))
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<String>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<String>>>(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else
                        callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.GET_ROUTES_ON_STATION);
    }

    /**
     * Returns information about station identified by station's int_id.
     * @param station_id ID of station.
     * @param callback callback to be triggered when finished.
     */
    public static void getStationById(String station_id, ApiCallback<StationById> callback) {
        new LppQuery()
                .addParams("station_id", station_id)
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<StationById> data = new Gson().fromJson(response, new TypeToken<ApiResponse<StationById>>(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else
                        callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.GET_STATION_BY_ID);
    }

    /**
     * Returns information about station identified by station's int_id.
     * @param station_int_id int ID of station.
     * @param callback callback to be triggered when finished.
     */
    public static void getStationById(int station_int_id, ApiCallback<StationById> callback) {
        new LppQuery()
                .addParams("station_int_id", String.valueOf(station_int_id))
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<StationById> data = new Gson().fromJson(response, new TypeToken<ApiResponse<StationById>>(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else
                        callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.GET_STATION_BY_ID);
    }

    /**
     * Returns information about all known stations.
     * @param callback callback to be triggered when finished.
     * @apiNote It takes a really long time! (~18541 ms), Big JSON size! (~235.29KB) => use with caution
     */
    public static void getStationById(ApiCallback<List<StationById>> callback) {
        new LppQuery()
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<StationById>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<StationById>>>(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else
                        callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.GET_STATION_BY_ID);
    }

    /**
     * Returns stations in specified radius of given location. Locations are ordered from nearest to furthest.
     * @param radius Radius for searching in meters.
     * @param lat Latitude.
     * @param lon Longitude.
     * @param callback callback to be triggered when finished.
     */
    public static void stationsInRange(int radius, double lat, double lon, ApiCallback<List<StationsInRange>> callback) {
        new LppQuery()
                .addParams("radius", String.valueOf(radius))
                .addParams("lat", String.valueOf(lat))
                .addParams("lon", String.valueOf(lon))
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<StationsInRange>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<StationsInRange>>>(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else
                        callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.STATIONS_IN_RANGE);
    }

    /**
     * Returns stations in specified radius of given location. Locations are ordered from nearest to furthest.
     * @param lat Latitude.
     * @param lon Longitude.
     * @param callback callback to be triggered when finished.
     * @apiNote Default radius is used (100m)
     */
    public static void stationsInRange(double lat, double lon, ApiCallback<List<StationsInRange>> callback) {
        new LppQuery()
                .addParams("lat", String.valueOf(lat))
                .addParams("lon", String.valueOf(lon))
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<StationsInRange>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<StationsInRange>>>(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else
                        callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.STATIONS_IN_RANGE);
    }

    /**
     * Returns detailed info about all stations.
     * @param callback callback to be triggered when finished.
     */
    public static void getAllStations(ApiCallback<List<AllStations>> callback) {
        new LppQuery()
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<AllStations>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<AllStations>>>(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else
                        callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.GET_ALL_STATIONS);
    }

    /**
     * Returns information about bus arrivals on specified station. Arrivals are ordered by Estimated Time of Arrival.
     * @param station_int_id int ID of station.
     * @param callback callback to be triggered when finished.
     */
    public static void liveBusArrival(int station_int_id, ApiCallback<List<LiveBusArrival>> callback) {
        new LppQuery()
                .addParams("station_int_id", String.valueOf(station_int_id))
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<LiveBusArrival>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<LiveBusArrival>>>(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else
                        callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.LIVE_BUS_ARRIVAL);
    }

    /**
     * Returns information about bus arrivals on specified station. Arrivals are ordered by Estimated Time of Arrival.
     * @param int_id int ID of station.
     * @param callback callback to be triggered when finished.
     * @apiNote V2 of liveBusArrival().
     */
    public static void liveBusArrivalV2(int int_id, ApiCallback<LiveBusArrivalV2> callback) {
        new LppQuery()
                .addParams("int_id", String.valueOf(int_id))
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<LiveBusArrivalV2> data = new Gson().fromJson(response, new TypeToken<ApiResponse<LiveBusArrivalV2>>(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else
                        callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.LIVE_BUS_ARRIVAL_V2);
    }

    /**
     * Returns recently updated GPS coordinates of buses in specified radius of given location.
     * @param radius Radius for searching in meters.
     * @param lat Latitude.
     * @param lon Longitude.
     * @param callback callback to be triggered when finished.
     */
    public static void busesInRange(int radius, double lat, double lon, ApiCallback<List<Bus>> callback) {
        new LppQuery()
                .addParams("radius", String.valueOf(radius))
                .addParams("lat", String.valueOf(lat))
                .addParams("lon", String.valueOf(lon))
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<Bus>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<Bus>>>(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else
                        callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.BUSES_IN_RANGE);
    }

    /**
     * Returns recently updated GPS coordinates of buses in specified radius of given location.
     * @param lat Latitude.
     * @param lon Longitude.
     * @param callback callback to be triggered when finished.
     * @apiNote Default radius is used (100m)
     */
    public static void busesInRange(double lat, double lon, ApiCallback<List<Bus>> callback) {
        new LppQuery()
                .addParams("lat", String.valueOf(lat))
                .addParams("lon", String.valueOf(lon))
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<Bus>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<Bus>>>(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else
                        callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.BUSES_IN_RANGE);
    }

    /**
     * Returns a list of buses and their locations for a specified route.
     * @param route_int_id Integer ID of route.
     * @param callback callback to be triggered when finished.
     */
    public static void busLocation(int route_int_id, ApiCallback<List<Bus>> callback) {

        new LppQuery()
                .addParams("route_int_id", String.valueOf(route_int_id))
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<Bus>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<Bus>>>(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else
                        callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.BUS_LOCATION);

    }

    /**
     * Returns all buses and their locations.
     * @param callback callback to be triggered when finished.
     */
    public static void busLocation(ApiCallback<List<Bus>> callback) {
        new LppQuery()
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<Bus>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<Bus>>>(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else
                        callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.BUS_LOCATION);
    }

    /**
     * Returns the next station a bus will arrive to, based on previous station location. If not enough data is in the DB (null route or null previous station), system will return error.
     * @param bus_id Registration number of bus (LPP-101...). Characters are trimmed, only 3 digits are needed to identify the bus.
     * @param callback callback to be triggered when finished.
     */
    public static void getNextStationFull(String bus_id, ApiCallback<NextStation> callback) {
        new LppQuery()
                .addParams("bus_id", bus_id)
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<NextStation> data = new Gson().fromJson(response, new TypeToken<ApiResponse<NextStation>>(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else
                        callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.GET_NEXT_STATION_FULL);
    }

}
