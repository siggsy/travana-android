package com.VegaSolutions.lpptransit.lppapi;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Map;

/**
 * Class for querying data from LPP API. All available API is saved in static Strings. Default OnCompleteListener prints out data and result code to LOG
 */
public class LppQuery extends AsyncTask<String, Void, String> {

    private static final String TAG = LppQuery.class.getSimpleName();

    public static final String SERVER_URL = "http://data.lpp.si";
    public static final String GET_API_INFO = "/info/getApiInfo";

    // Route API
    public static final String GET_ROUTE_DETAILS = "/routes/getRouteDetails"; // parameters required (route_int_id=341)
    public static final String GET_ROUTE_GROUPS = "/routes/getRouteGroups";
    public static final String GET_ROUTES = "/routes/getRoutes"; // 1 parameter required (route_id=11705c09-1316-4c46-a0e1-0df468d24deb) / (route_name=6)
    public static final String GET_STATIONS_ON_ROUTE = "/routes/getStationsOnRoute"; // 1 parameter required (route_id=d532a61e-620c-4eb1-9a76-2a9e989213f5) / (route_int_id=346)
    public static final String GET_ROUTE_PARENTS = "/routes/getRouteParents"; // 1 parameter required (route_id=d532a61e-620c-4eb1-9a76-2a9e989213f5) / (route_int_id=346)
    private static final String GET_GTFS = "/routes/getGTFS"; // NOT USEFUL (downloads file)
    public static final String GET_STATION_LIST = "/routes/v2/getStationList"; // unknown parameters

    // Stations API
    public static final String GET_ROUTES_ON_STATION = "/stations/getRoutesOnStation";
    public static final String GET_ROUTES_ON_STATION_V2 = "/stations/v2/getRoutesOnStation";
    public static final String GET_STATIONS_BY_ID = "/stations/getStationById";
    public static final String GET_STATIONS_BY_ID_V2 = "/stations/v2/getStationById";
    public static final String STATIONS_IN_RANGE = "/stations/stationsInRange";
    public static final String GET_ALL_STATIONS = "/stations/getAllStations";

    // Timetables API
    public static final String GET_ROUTE_DEPARTURES = "/timetables/getRouteDepartures";
    public static final String LIVE_BUS_ARRIVAL = "/timetables/liveBusArrival";
    public static final String LIVE_BUS_ARRIVAL_V2 = "/timetables/v2/liveBusArrival";
    public static final String GET_TIMETABLES_ON_STATION_V2 = "/timetables/v2/getTimetablesOnStation";

    // Bus API
    public static final String BUS_LOCATION = "/bus/busLocation"; // API key required
    public static final String BUSES_IN_RANGE = "/bus/busesInRange"; // API key required
    public static final String GET_NEXT_STATION_FULL = "/bus/getNextStationFull";
    public static final String GET_DRIVER = "/bus/getDriver"; // API key required
    public static final String GET_BUS_FUTURE_DATA = "/bus/getBusFutureData";



    // Url parameters
    private String params = "";
    // Default onCompleteListener
    private OnCompleteListener onCompleteListener = (data, returnCode, success) -> {
        if (success) {
            Log.i(TAG, "Return Code: " + returnCode);
            Log.i(TAG, "Body : " + returnCode);
        } else {
            Log.e(TAG, "Failed to establish connection");
        }
    };


    /**
     * set required parameters
     * @param paramsMap map of parameters (String, String)
     * @return current instance for chaining
     */
    public LppQuery setParams(@Nullable Map<String, String> paramsMap) {

        if (paramsMap != null && paramsMap.size() != 0) {
            // convert to url
            StringBuilder builder = new StringBuilder("?");
            for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
                builder.append(entry.getKey()).append("=").append(entry.getValue());
                builder.append("&");
            }
            this.params = builder.substring(0, builder.length() - 1);
        }
        return this;
    }

    /**
     * set required parameter
     * @param key value name
     * @param value the value
     * @return current instance for chaining
     */
    public LppQuery setParams(@NonNull String key, @NonNull String value) {
        params = "?" + key + "=" + value;
        return this;
    }


    /**
     * Executed code when query completed
     * @param onCompleteListener query callback. See LppQuery.OnCompleteListener for more info
     */
    public LppQuery setOnCompleteListener(@NonNull OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
        return this;
    }


    @Override
    protected String doInBackground(String... apis) {

        for (String api : apis) {

            try {
                Connection.Response r = Jsoup.connect(SERVER_URL + api + params).ignoreContentType(true).execute();
                onCompleteListener.onComplete(r.body(), r.statusCode(), true);
            } catch (HttpStatusException e) {
                e.printStackTrace();
                onCompleteListener.onComplete(null, e.getStatusCode(), false);
            } catch (IOException e) {
                onCompleteListener.onComplete(null, -1, false);
            }

        }

        return null;
    }


    public interface OnCompleteListener {

        /**
         * Executed when HTML GET completed
         * @param response String representing response body.
         * @param statusCode int equivalent to HTML status code. "-1" means IOException
         * @param success boolean if connection was successful.
         */
        void onComplete(String response, int statusCode, boolean success);
    }

}
