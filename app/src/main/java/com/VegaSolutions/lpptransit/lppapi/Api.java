package com.VegaSolutions.lpptransit.lppapi;

import com.VegaSolutions.lpptransit.lppapi.responseobjects.ApiResponse;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.ArrivalWrapper;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Bus;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.BusOnRoute;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.DepartureWrapper;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Route;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.RouteOnStation;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Station;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.StationOnRoute;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.TimetableWrapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.List;

public class Api {

    public static void busDetails_name(String bus_name, ApiCallback<List<Bus>> callback) {

        new LppQuery()
                .addParams("bus-name", bus_name)
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<Bus>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<Bus>>>(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.BUS_DETAILS);

    }
    public static void busDetails_vin(String bus_vin, ApiCallback<Bus> callback) {

        new LppQuery()
                .addParams("bus-vin", bus_vin)
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<Bus> data = new Gson().fromJson(response, new TypeToken<ApiResponse<Bus>>(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.BUS_DETAILS);

    }
    public static void busDetails_all(ApiCallback<List<Bus>> callback) {
        new LppQuery()
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<Bus>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<Bus>>>(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.BUS_DETAILS);
    }
    public static void busesOnRoute(String routeGroupNumber, ApiCallback<List<BusOnRoute>> callback) {
        new LppQuery()
                .addParams("route-group-number", routeGroupNumber)
                .addParams("specific", "1")
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<BusOnRoute>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<BusOnRoute>>>(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.BUSES_ON_ROUTE);
    }

    public static void activeRoutes(ApiCallback<List<Route>> callback) {
        new LppQuery()
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<Route>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<Route>>>(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.ACTIVE_ROUTES);
    }

    public static void routes(ApiCallback<List<Route>> callback) {
        new LppQuery()
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<Route>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<Route>>>(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.ROUTES);
    }

    public static void routes(String route_id, ApiCallback<List<Route>> callback) {
        new LppQuery()
                .addParams("route-id", route_id)
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<Route>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<Route>>>(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.ROUTES);
    }

    public static void stationsOnRoute(String trip_id, ApiCallback<List<StationOnRoute>> callback) {
        new LppQuery()
                .addParams("trip-id", trip_id)
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<StationOnRoute>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<StationOnRoute>>>(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.STATIONS_ON_ROUTE);

    }

    public static void arrival(String station_code, ApiCallback<ArrivalWrapper> callback) {
        new LppQuery()
                .addParams("station-code", station_code)
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<ArrivalWrapper> data = new Gson().fromJson(response, new TypeToken<ApiResponse<ArrivalWrapper>>(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.ARRIVAL);
    }

    public static void routesOnStation(int station_code, ApiCallback<List<RouteOnStation>> callback) {
        new LppQuery()
                .addParams("station-code", String.valueOf(station_code))
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<RouteOnStation>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<RouteOnStation>>>(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.ROUTES_ON_STATION);
    }

    public static void stationDetails(int station_code, boolean show_subroutes, ApiCallback<Station> callback) {
            new LppQuery()
                    .addParams("station-code", String.valueOf(station_code))
                    .addParams("show-subroutes", show_subroutes ? "1" : "0")
                    .setOnCompleteListener((response, statusCode, success) -> {
                        if (success) {
                            ApiResponse<Station> data = new Gson().fromJson(response, new TypeToken<ApiResponse<Station>>(){}.getType());
                            callback.onComplete(data, statusCode, true);
                        } else callback.onComplete(null, statusCode, false);
                    })
                    .execute(LppQuery.STATION_DETAILS);
    }

    public static void stationDetails(boolean show_subroutes, ApiCallback<List<Station>> callback) {
        new LppQuery()
                .addParams("show-subroutes", show_subroutes ? "1" : "0")
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<Station>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<Station>>>(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.STATION_DETAILS);
    }

    public static void timetable(int station_code, int next_hours, int previous_hours, ApiCallback<TimetableWrapper> callback, int... route_group_numbers) {
        LppQuery q = new LppQuery()
                .addParams("station-code", String.valueOf(station_code))
                .addParams("next-hours", String.valueOf(next_hours))
                .addParams("previous-hours", String.valueOf(previous_hours))
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<TimetableWrapper> data = new Gson().fromJson(response, new TypeToken<ApiResponse<TimetableWrapper>>(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else callback.onComplete(null, statusCode, false);
                });
        for (int gnum : route_group_numbers)
            q.addParams("route-group-number", String.valueOf(gnum));
        q.execute(LppQuery.TIMETABLE);
    }

    public static void routeDepartures(String trip_id, String route_id, ApiCallback<DepartureWrapper> callback) {
        new LppQuery()
                .addParams("trip-id", trip_id)
                .addParams("route-id", route_id)
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<DepartureWrapper> data = new Gson().fromJson(response, new TypeToken<ApiResponse<DepartureWrapper>>(){}.getType());
                        callback.onComplete(data, statusCode, true);
                    } else callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.ROUTE_DEPARTURES);
    }

}
