package com.VegaSolutions.lpptransit.lppapi;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.VegaSolutions.lpptransit.BuildConfig;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LppQuery extends AsyncTask<String, Void, String> {

    private static final String TAG = LppQuery.class.getSimpleName();
    public static final String SERVER_URL = "http://194.33.12.32/api";

    // API PARAMETERS:
    // '?' -> optional,
    // '!' -> required,
    // '!|' -> xor,
    // '|' -> or,
    // '&' -> and.
    // '#' -> multiple allowed;

    // Bus
    public static final String BUS_DETAILS = "/bus/bus-details";                    // (?bus-name=468) !| (?bus-vin=ZCFC270C305997503) !| (?bus-id=?)
    public static final String BUSES_ON_ROUTE = "/bus/buses-on-route";
    public static final String DRIVER = "/bus/driver";                              // (?driver-id=36066995138405380)

    // Route
    public static final String ACTIVE_ROUTES = "/route/active-routes";              //
    public static final String ROUTES = "/route/routes";                            // (?route-id=A48D5D5E-1A10-4616-86BE-65B059E0A371)
    public static final String STATIONS_ON_ROUTE = "/route/stations-on-route";      // (?trip-id=A3295EA8-3404-4D34-9C6A-1604EFD15E40)

    // Station
    public static final String ARRIVAL = "/station/arrival";                        // (!station-code=600011)
    public static final String ROUTES_ON_STATION = "/station/routes-on-station";    // (!station-code=600011)
    public static final String STATION_DETAILS = "/station/station-details";        // (?station-code=600011) | (?show-subroutes=1)
    public static final String TIMETABLE = "/station/timetable";                    // (!station-code=600011) & (!#route-group-number=6) | (?next-hours=4) | (?previous-hours=2)

    // Timetable
    public static final String ROUTE_DEPARTURES = "/timetable/route-departures";    // (!trip-id=a3295EA8-3404-4D34-9C6A-1604EFD15E40) & (!route-id=117C48BE-5CB3-4030-92FC-E22371A0779F)

    // Url parameters
    private StringBuilder params = new StringBuilder();
    // Default onCompleteListener
    private LppQuery.OnCompleteListener onCompleteListener = (data, returnCode, success) -> {
        if (success) {
            Log.i(TAG, "Return Code: " + returnCode);
            Log.i(TAG, "Body : " + returnCode);
        } else {
            Log.e(TAG, "Failed to establish connection");
        }
    };

    /**
     * add required parameter
     * @param key value name
     * @param value the value
     * @return current instance for chaining
     */
    public LppQuery addParams(@NonNull String key, @NonNull String value) {
        if (params.length() == 0) params.append("?");
        else params.append("&");
        params.append(key).append('=').append(value);
        return this;
    }

    public LppQuery setOnCompleteListener(@NonNull LppQuery.OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
        return this;
    }

    @Override
    protected String doInBackground(String... apis) {
        for (String api : apis) {

            try {
                Connection.Response r = Jsoup.connect(SERVER_URL + api + params).ignoreContentType(true).header("apikey", BuildConfig.LPP_API_KEY).timeout(0).execute(); //.header("apikey", BuildConfig.LPP_API_KEY)
                Log.i(TAG, r.body());
                onCompleteListener.onComplete(r.body(), r.statusCode(), true);
            } catch (HttpStatusException e) {
                e.printStackTrace();
                onCompleteListener.onComplete(null, e.getStatusCode(), false);
            } catch (IOException e) {
                e.printStackTrace();
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
