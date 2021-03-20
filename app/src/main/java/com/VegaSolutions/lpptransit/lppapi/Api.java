package com.VegaSolutions.lpptransit.lppapi;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.VegaSolutions.lpptransit.BuildConfig;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Api {

    // Documentation: http://194.33.12.32/doc/
    public static final String DATA_URL = "https://data.lpp.si/api";
    public static final String DETOUR_URL = "https://www.lpp.si";

    // Bus
    public static final String BUS_DETAILS = "/bus/bus-details";
    public static final String BUSES_ON_ROUTE = "/bus/buses-on-route";
    public static final String DRIVER = "/bus/driver";

    // Route
    public static final String ACTIVE_ROUTES = "/route/active-routes";
    public static final String ROUTES = "/route/routes";
    public static final String STATIONS_ON_ROUTE = "/route/stations-on-route";
    public static final String ARRIVALS_ON_ROUTE = "/route/arrivals-on-route";

    // Station
    public static final String ARRIVAL = "/station/arrival";
    public static final String ROUTES_ON_STATION = "/station/routes-on-station";
    public static final String STATION_DETAILS = "/station/station-details";
    public static final String TIMETABLE = "/station/timetable";

    // Timetable
    public static final String ROUTE_DEPARTURES = "/timetable/route-departures";

    // Detours
    public static final String DETOURS = "/javni-prevoz/obvozi/";

    public static final String TAG = "Api";
    private static final OkHttpClient httpClient = new OkHttpClient();
    private static final Headers headers = Headers.of(
        "apikey", BuildConfig.LPP_API_KEY,
        "Content-Type", "application/json",
        "User-Agent", "OkHttp Bot",
        "Accept", "",
        "Accept-Encoding", "identity",
        "Cache-Control", "no-cache",
        "Accept-Encoding", "gzip, deflate"
    );

    public static void busDetailsName(String busName, ApiCallback<List<Bus>> callback) {
        request(DATA_URL + BUS_DETAILS, jsonCallback(callback, new TypeToken<ApiResponse<List<Bus>>>(){}.getType()),
                "bus-name", busName
        );
    }
    public static void busDetailsVin(String busVin, ApiCallback<Bus> callback) {
        request(DATA_URL + BUS_DETAILS, jsonCallback(callback, new TypeToken<ApiResponse<Bus>>(){}.getType()),
                "bus-vin", busVin);
    }
    public static void busDetailsAll(ApiCallback<List<Bus>> callback) {
        request(DATA_URL + BUS_DETAILS, jsonCallback(callback, new TypeToken<ApiResponse<List<Bus>>>(){}.getType()));
    }
    public static void busesOnRoute(String routeGroupNumber, ApiCallback<List<BusOnRoute>> callback) {
        request(DATA_URL + BUSES_ON_ROUTE, jsonCallback(callback, new TypeToken<ApiResponse<List<BusOnRoute>>>(){}.getType()),
                "route-group-number", routeGroupNumber);
    }

    public static void activeRoutes(ApiCallback<List<Route>> callback) {
        request(DATA_URL + ACTIVE_ROUTES, jsonCallback(callback, new TypeToken<ApiResponse<List<Route>>>(){}.getType()));
    }

    public static void routes(ApiCallback<List<Route>> callback) {
        request(DATA_URL + ROUTES, jsonCallback(callback, new TypeToken<ApiResponse<List<Route>>>(){}.getType()));
    }

    public static void routes(String routeId, ApiCallback<List<Route>> callback) {
        request(DATA_URL + ROUTES, jsonCallback(callback, new TypeToken<ApiResponse<List<Route>>>(){}.getType()),
                "route-id", routeId);
    }

    public static void stationsOnRoute(String tripId, ApiCallback<List<StationOnRoute>> callback) {
        request(DATA_URL + STATIONS_ON_ROUTE, jsonCallback(callback, new TypeToken<ApiResponse<List<StationOnRoute>>>(){}.getType()),
                "trip-id", tripId);
    }

    public static void arrivalsOnRoute(String tripId, ApiCallback<List<ArrivalOnRoute>> callback) {
        request(DATA_URL + ARRIVALS_ON_ROUTE, jsonCallback(callback, new TypeToken<ApiResponse<List<ArrivalOnRoute>>>(){}.getType()),
                "trip-id", tripId);
    }

    public static void arrival(String stationCode, ApiCallback<ArrivalWrapper> callback) {
        request(DATA_URL + ARRIVAL, jsonCallback(callback, new TypeToken<ApiResponse<ArrivalWrapper>>(){}.getType()),
                "station-code", stationCode);
    }

    public static void routesOnStation(String stationCode, ApiCallback<List<RouteOnStation>> callback) {
        request(DATA_URL + ROUTES_ON_STATION, jsonCallback(callback, new TypeToken<ApiResponse<List<RouteOnStation>>>(){}.getType()),
                "station-code", stationCode);
    }

    public static void stationDetails(String stationCode, boolean showSubroutes, ApiCallback<Station> callback) {
        request(DATA_URL + STATION_DETAILS, jsonCallback(callback, new TypeToken<ApiResponse<Station>>(){}.getType()),
                "station-code", stationCode,
                "show-subroutes", showSubroutes ? "1" : "0");
    }

    public static void stationDetails(boolean showSubroutes, ApiCallback<List<Station>> callback) {
        request(
                DATA_URL + STATION_DETAILS,
                jsonCallback(callback, new TypeToken<ApiResponse<List<Station>>>(){}.getType()),
                "show-subroutes", showSubroutes ? "1" : "0"
        );
    }

    public static void timetable(
            String stationCode,
            int nextHours,
            int previousHours,
            ApiCallback<TimetableWrapper> callback,
            int... routeGroupNumbers
    ) {
        String[] params = {
                "station-code", stationCode,
                "next-hours", String.valueOf(nextHours),
                "previous-hours", String.valueOf(previousHours),
        };

        String[] dynamicParams = new String[routeGroupNumbers.length * 2];
        for (int i = 0, j = 0; i < routeGroupNumbers.length * 2 && j < routeGroupNumbers.length; i += 2, j++) {
            dynamicParams[i] = "route-group-number";
            dynamicParams[i+1] = String.valueOf(routeGroupNumbers[j]);
        }

        // Concatenate parameters
        String[] finalParams = new String[params.length + dynamicParams.length];
        int index = 0;
        for (String param : params) {
            finalParams[index] = param;
            index++;
        }
        for (String param : dynamicParams) {
            finalParams[index] = param;
            index++;
        }

        request(DATA_URL + TIMETABLE, jsonCallback(callback, new TypeToken<ApiResponse<TimetableWrapper>>(){}.getType()), finalParams);
    }

    public static void routeDepartures(String tripId, String routeId, ApiCallback<DepartureWrapper> callback) {
        request(
                DATA_URL + ROUTE_DEPARTURES,
                jsonCallback(callback, new TypeToken<ApiResponse<DepartureWrapper>>(){}.getType()),
                "trip-id", tripId,
                "route-id", routeId
        );
    }

    public static void getDetours(ApiCallback<List<DetourInfo>> callback) {
        request(DETOUR_URL + DETOURS, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onComplete(null, -1, false);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        List<DetourInfo> data = getDetours(response.body().string());
                        ApiResponse<List<DetourInfo>> apiResponse = new ApiResponse<>(true, data);
                        callback.onComplete(apiResponse, response.code(), true);
                    } catch (Exception e) {
                        callback.onComplete(null, -4, false);
                    }
                } else {
                    callback.onComplete(null, response.code(), false);
                }
            }
        });
    }

    public static void getDetourDetailed(String link, String title, String time, ApiCallback<DetourInfo> callback) {
        request(DETOUR_URL + DETOURS + link, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onComplete(null, -1, false);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        DetourInfo di = new DetourInfo(title, time, null, response.body().string(), null);
                        ApiResponse<DetourInfo> apiResponse = new ApiResponse<>(true, di);
                        callback.onComplete(apiResponse, response.code(), true);
                    } catch (Exception e) {
                        callback.onComplete(null, -4, false);
                    }
                } else {
                    callback.onComplete(null, response.code(), false);
                }
            }
        });
    }

    public static List<String> getSavedSearchItemsIds(Activity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences("app", Context.MODE_PRIVATE);
        Set<String> searchItems = sharedPref.getStringSet("saved_search_items", null);

        if (searchItems == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(searchItems);
    }

    public static void addSavedSearchedItemsIds(String id, Activity activity) {

        SharedPreferences sharedPref = activity.getSharedPreferences("app", Context.MODE_PRIVATE);
        Set<String> searchItems = sharedPref.getStringSet("saved_search_items", null);

        if (searchItems == null) {
            searchItems = new HashSet<>();
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

    private static List<DetourInfo> getDetours(String html) {

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

    private static <T> Callback jsonCallback(ApiCallback<T> callback, Type type) {
        return new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onComplete(null, -1, false);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                if (response.isSuccessful()) {
                    try {
                        String r = response.body().string();
                        ApiResponse<T> data = new Gson().fromJson(r, type);
                        callback.onComplete(data, response.code(), true);
                    } catch (IOException e) {

                        e.printStackTrace();
                        Log.i(TAG, response.request().url().toString());
                        callback.onComplete(null, -4, false);

                    }
                } else {
                    callback.onComplete(null, response.code(), false);
                }
            }
        };
    }
    private static void request(String url, Callback callback, String... params) {
        String paramString = getParamString(params);
        Request request = new Request.Builder()
                .url(url + paramString)
                .headers(headers)
                .build();

        httpClient.newCall(request).enqueue(callback);
    }
    private static String getParamString(String[] params) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.length - 1; i++) {
            sb.append(params[i]).append('=').append(params[i+1]).append("&");
        }
        if (params.length > 0) {
            sb.insert(0, "?");
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

}
