package com.VegaSolutions.lpptransit.lppapi;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

/**
 * Class for querying data from LPP API. All available API is saved in static Strings. Default OnCompleteListener prints out data and result code to LOG
 */
public class LppQuery extends AsyncTask<String, Void, String> {

    private static final String TAG = LppQuery.class.getSimpleName();

    public static final String SERVER_URL = "http://data.lpp.si";
    public static final String GET_API_INFO = "/info/getApiInfo";

    // Route API
    public static final String GET_ROUTE_DETAILS = "/routes/getRouteDetails";
    public static final String GET_ROUTE_GROUPS = "/routes/getRouteGroups";
    public static final String GET_ROUTES = "/routes/getRoutes";
    public static final String GET_STATIONS_ON_ROUTE = "/routes/getStationsOnRoute";
    public static final String GET_ROUTE_PARENTS = "/routes/getRouteParents";
    public static final String GET_GTFS = "/routes/getGTFS";
    public static final String GET_STATION_LIST = "/routes/v2/getStationList";

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
    public static final String GET_DRIVER = "/bus/getDriver"; // API ket required
    public static final String GET_BUS_FUTURE_DATA = "/bus/getBusFutureData";

    private OnCompleteListener onCompleteListener = (data, returnCode, success) -> {
        if (success) {
            Log.i(TAG, "Return Code: " + returnCode);
            Log.i(TAG, "Body : " + returnCode);
        } else {
            Log.e(TAG, "Failed to establish connection");
        }
    };

    /**
     * Executed code when query completed
     * @param onCompleteListener query callback. See LppQuery.OnCompleteListener for more info
     */
    public void addOnCompleteListener(OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }

    @Override
    protected String doInBackground(String... apis) {

        for (String api : apis) {

            try {
                Connection.Response r = Jsoup.connect(SERVER_URL + api).execute();
                onCompleteListener.onComplete(r.body(), r.statusCode(), true);
            } catch (IOException e) {
                e.printStackTrace();
                onCompleteListener.onComplete(null, -1, false);
            }

        }

        return null;
    }

    public interface OnCompleteListener {

        /**
         * @param response String representing response body
         * @param returnCode int equivalent to HTML response code. ReturnCode -1 means connection failed.
         * @param success boolean if connection was successful
         */

        void onComplete(String response, int returnCode, boolean success);
    }

}
