package com.VegaSolutions.lpptransit.lppapi;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.VegaSolutions.lpptransit.lppapi.responseobjects.ApiResponse;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.ArrivalOnRoute;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.ArrivalWrapper;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Bus;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.BusOnRoute;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.DepartureWrapper;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.DetourInfo;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Route;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.RouteOnStation;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Station;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.StationOnRoute;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.TimetableWrapper;
import com.VegaSolutions.lpptransit.ui.activities.SearchActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.OkHttpClient;

public class Api {

    public static final String TAG = "Api";
    private OkHttpClient httpClient;

    public Api(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public static void busDetails_name(String bus_name, ApiCallback<List<Bus>> callback) {

        new LppQuery(LppQuery.BUS_DETAILS)
                .addParams("bus-name", bus_name)
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<Bus>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<Bus>>>(){}.getType());
                        callback.onComplete(data, statusCode, data != null);
                    } else callback.onComplete(null, statusCode, false);
                })
                .start();

    }
    public static void busDetails_vin(String bus_vin, ApiCallback<Bus> callback) {

        new LppQuery(LppQuery.BUS_DETAILS)
                .addParams("bus-vin", bus_vin)
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<Bus> data = new Gson().fromJson(response, new TypeToken<ApiResponse<Bus>>(){}.getType());
                        callback.onComplete(data, statusCode, data != null);
                    } else callback.onComplete(null, statusCode, false);
                })
                .start();

    }
    public static void busDetails_all(ApiCallback<List<Bus>> callback) {
        new LppQuery(LppQuery.BUS_DETAILS)
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<Bus>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<Bus>>>(){}.getType());
                        callback.onComplete(data, statusCode, data != null);
                    } else callback.onComplete(null, statusCode, false);
                })
                .start();
    }
    public static void busesOnRoute(String routeGroupNumber, ApiCallback<List<BusOnRoute>> callback) {
        new LppQuery(LppQuery.BUSES_ON_ROUTE)
                .addParams("route-group-number", routeGroupNumber)
                .addParams("specific", "1")
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<BusOnRoute>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<BusOnRoute>>>(){}.getType());
                        callback.onComplete(data, statusCode, data != null);
                    } else callback.onComplete(null, statusCode, false);
                })
                .start();
    }

    public static void activeRoutes(ApiCallback<List<Route>> callback) {
        new LppQuery(LppQuery.ACTIVE_ROUTES)
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<Route>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<Route>>>(){}.getType());
                        callback.onComplete(data, statusCode, data != null);
                    } else callback.onComplete(null, statusCode, false);
                })
                .start();
    }

    public static void routes(ApiCallback<List<Route>> callback) {
        new LppQuery(LppQuery.ROUTES)
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<Route>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<Route>>>(){}.getType());
                        callback.onComplete(data, statusCode, data != null);
                    } else callback.onComplete(null, statusCode, false);
                })
                .start();
    }

    public static void routes(String route_id, ApiCallback<List<Route>> callback) {
        new LppQuery(LppQuery.ROUTES)
                .addParams("route-id", route_id)
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<Route>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<Route>>>(){}.getType());
                        callback.onComplete(data, statusCode, data != null);
                    } else callback.onComplete(null, statusCode, false);
                })
                .start();
    }

    public static void stationsOnRoute(String trip_id, ApiCallback<List<StationOnRoute>> callback) {
        new LppQuery(LppQuery.STATIONS_ON_ROUTE)
                .addParams("trip-id", trip_id)
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<StationOnRoute>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<StationOnRoute>>>(){}.getType());
                        callback.onComplete(data, statusCode, data != null);
                    } else callback.onComplete(null, statusCode, false);
                })
                .start();

    }

    public static void arrivalsOnRoute(String trip_id, ApiCallback<List<ArrivalOnRoute>> callback) {
        new LppQuery(LppQuery.ARRIVALS_ON_ROUTE)
                .addParams("trip-id", trip_id)
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<ArrivalOnRoute>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<ArrivalOnRoute>>>(){}.getType());
                        callback.onComplete(data, statusCode, data != null);
                    } else callback.onComplete(null, statusCode, false);
                })
                .start();
    }

    public static void arrival(String station_code, ApiCallback<ArrivalWrapper> callback) {
        new LppQuery(LppQuery.ARRIVAL)
                .addParams("station-code", station_code)
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<ArrivalWrapper> data = new Gson().fromJson(response, new TypeToken<ApiResponse<ArrivalWrapper>>(){}.getType());
                        callback.onComplete(data, statusCode, data != null);
                    } else callback.onComplete(null, statusCode, false);
                })
                .start();
    }

    public static void routesOnStation(int station_code, ApiCallback<List<RouteOnStation>> callback) {
        new LppQuery(LppQuery.ROUTES_ON_STATION)
                .addParams("station-code", String.valueOf(station_code))
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<RouteOnStation>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<RouteOnStation>>>(){}.getType());
                        callback.onComplete(data, statusCode, data != null);
                    } else callback.onComplete(null, statusCode, false);
                })
                .start();
    }

    public static void stationDetails(int station_code, boolean show_subroutes, ApiCallback<Station> callback) {
            new LppQuery(LppQuery.STATION_DETAILS)
                    .addParams("station-code", String.valueOf(station_code))
                    .addParams("show-subroutes", show_subroutes ? "1" : "0")
                    .setOnCompleteListener((response, statusCode, success) -> {
                        if (success) {
                            ApiResponse<Station> data = new Gson().fromJson(response, new TypeToken<ApiResponse<Station>>(){}.getType());
                            callback.onComplete(data, statusCode, data != null);
                        } else callback.onComplete(null, statusCode, false);
                    })
                    .start();
    }

    public static void stationDetails(boolean show_subroutes, ApiCallback<List<Station>> callback) {
        new LppQuery(LppQuery.STATION_DETAILS)
                .addParams("show-subroutes", show_subroutes ? "1" : "0")
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<List<Station>> data = new Gson().fromJson(response, new TypeToken<ApiResponse<List<Station>>>(){}.getType());
                        callback.onComplete(data, statusCode, data != null);
                    } else callback.onComplete(null, statusCode, false);
                })
                .start();
    }

    public static void timetable(int station_code, int next_hours, int previous_hours, ApiCallback<TimetableWrapper> callback, int... route_group_numbers) {
        LppQuery q = new LppQuery(LppQuery.TIMETABLE)
                .addParams("station-code", String.valueOf(station_code))
                .addParams("next-hours", String.valueOf(next_hours))
                .addParams("previous-hours", String.valueOf(previous_hours))
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<TimetableWrapper> data = new Gson().fromJson(response, new TypeToken<ApiResponse<TimetableWrapper>>(){}.getType());
                        callback.onComplete(data, statusCode, data != null);
                    } else callback.onComplete(null, statusCode, false);
                });
        for (int gnum : route_group_numbers)
            q.addParams("route-group-number", String.valueOf(gnum));

        q.start();
    }

    public static void routeDepartures(String trip_id, String route_id, ApiCallback<DepartureWrapper> callback) {
        new LppQuery(LppQuery.ROUTE_DEPARTURES)
                .addParams("trip-id", trip_id)
                .addParams("route-id", route_id)
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        ApiResponse<DepartureWrapper> data = new Gson().fromJson(response, new TypeToken<ApiResponse<DepartureWrapper>>(){}.getType());
                        callback.onComplete(data, statusCode, data != null);
                    } else callback.onComplete(null, statusCode, false);
                })
                .start();
    }

    public static void getDetours(ApiCallback<List<DetourInfo>> callback) {
        new LppQuery2(LppQuery2.DETOURS)
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {

                        try{
                            List<DetourInfo> data = getDetours(response);
                            ApiResponse<List<DetourInfo>> r = new ApiResponse<List<DetourInfo>>(true, data);
                            callback.onComplete(r, statusCode, true);
                        }catch (Exception e){
                            callback.onComplete(null, statusCode, false);
                        }
                    } else callback.onComplete(null, statusCode, false);
                })
                .start();
    }

    public static void getDetourDetailed(String link, String title, String time, ApiCallback<DetourInfo> callback) {
        new LppQuery2(LppQuery2.DETOURS + link)
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {

                        try{

                            DetourInfo di = new DetourInfo(title, time, null, response, null);
                            ApiResponse<DetourInfo> apiResponse = new ApiResponse<>(true, di);

                            callback.onComplete(apiResponse, statusCode, true);

                        }catch (Exception e){
                            callback.onComplete(null, statusCode, false);
                        }
                    } else callback.onComplete(null, statusCode, false);
                })
                .start();
    }

    public static List<String> getSavedSearchItemsIds(Activity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences("app", Context.MODE_PRIVATE);
        Set<String> searchItems = sharedPref.getStringSet("saved_search_items", null);

        if (searchItems == null) {
            return new ArrayList<>();
        }

        return new ArrayList<String>(searchItems);
    }

    public static void addSavedSearchedItemsIds(String id, Activity activity) {

        SharedPreferences sharedPref = activity.getSharedPreferences("app", Context.MODE_PRIVATE);
        Set<String> searchItems = sharedPref.getStringSet("saved_search_items", null);

        if (searchItems == null) {
            searchItems = new HashSet<String>();
        }

        ArrayList<String> searchItemsArrayList = new ArrayList<>(searchItems);

        //Add item to the end if it was recently added
        searchItemsArrayList.add(id);

        if (searchItemsArrayList.size() > 20) {
            searchItemsArrayList.remove(0);
        }

        searchItems.clear();
        searchItems.addAll(searchItemsArrayList);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.putStringSet("saved_search_items", searchItems);
        editor.apply();
    }

    private static List<DetourInfo> getDetours(String html) throws Exception {

        String s = html;

        List<DetourInfo> list = new ArrayList<>();

        String main_html_word = "views-field views-field-nothing";
        String main_html_word_title = "content__box--title";
        String main_html_word_time = "content__box--date";

        while(s.contains(main_html_word)) {

            s = s.substring(s.indexOf(main_html_word) + main_html_word.length());
            s = s.substring(s.indexOf(main_html_word_title) + main_html_word_title.length());
            s = s.substring(s.indexOf("href"));

            String href = s.substring(s.indexOf("href") + 6, s.indexOf(">") -1);
            s = s.substring(s.indexOf(href) + href.length());

            String title = s.substring(s.indexOf(">") + 1, s.indexOf("<"));

            s = s.substring(s.indexOf(title) + title.length());
            s = s.substring(s.indexOf(main_html_word_time) + main_html_word_title.length() + 1);

            String date = s.substring(0, s.indexOf("<"));

            DetourInfo df = new DetourInfo(title, date, href);

            list.add(df);
        }

    return list;
    }
}
