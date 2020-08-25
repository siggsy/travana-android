package com.VegaSolutions.lpptransit.lppapi;

import android.util.Log;

import androidx.annotation.NonNull;

import com.VegaSolutions.lpptransit.BuildConfig;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

public class LppQuery2 extends Thread {

    //http://194.33.12.32/doc/                                                      //documentation

    private static final String TAG = LppQuery2.class.getSimpleName();
    public static final String SERVER_URL = "https://www.lpp.si";

    public static final String TIMETABLE = "/station/timetable";                    // (!station-code=600011) & (!#route-group-number=6) | (?next-hours=4) | (?previous-hours=2)

    // Timetable
    public static final String DETOURS = "/javni-prevoz/obvozi/";

    private static Map<String, String> headers = new HashMap<>();
    static {
        headers.put("apikey", BuildConfig.LPP_API_KEY);
        //headers.put("Content-Type", "application/json");
        headers.put("User-Agent", "OkHttp Bot");
        headers.put("Accept", "");
        headers.put("Cache-Control", "no-cache");
        headers.put("Accept-Encoding", "gzip, deflate");
    }

    public LppQuery2(String URL){
        this.URL = URL;
    }

    private String URL;
    // Url parameters
    private StringBuilder params = new StringBuilder();
    // Default onCompleteListener
    private LppQuery2.OnCompleteListener onCompleteListener = (data, returnCode, success) -> {
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
    LppQuery2 addParams(@NonNull String key, @NonNull String value) {
        if (params.length() == 0) params.append("?");
        else params.append("&");
        params.append(key).append('=').append(value);
        return this;
    }

    LppQuery2 setOnCompleteListener(@NonNull LppQuery2.OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
        return this;
    }

    @Override
    public void run() {

            try {
                Connection.Response r = Jsoup.connect(SERVER_URL + URL + params).ignoreContentType(true).headers(headers).timeout(20000).execute();
                //Log.i(TAG, r.body());
                onCompleteListener.onComplete(r.body(), r.statusCode(), true);
            } catch (HttpStatusException e) {
                e.printStackTrace();
                onCompleteListener.onComplete(null, e.getStatusCode(), false);
            } catch(SocketTimeoutException e) {
                e.printStackTrace();
                onCompleteListener.onComplete(null, -2, false);
            } catch (IOException e) {
                e.printStackTrace();
                onCompleteListener.onComplete(null, -1, false);
            } catch (Exception e) {
                e.printStackTrace();
                onCompleteListener.onComplete(null, -3, false);
            }

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
