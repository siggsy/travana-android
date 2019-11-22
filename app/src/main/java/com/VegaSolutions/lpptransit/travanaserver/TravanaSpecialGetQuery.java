package com.VegaSolutions.lpptransit.travanaserver;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import org.jsoup.HttpStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import okhttp3.Credentials;
import okhttp3.Request;
import okhttp3.Response;

public class TravanaSpecialGetQuery implements Runnable {

    private static final String TAG = TravanaSpecialGetQuery.class.getSimpleName();

    private StringBuilder params = new StringBuilder();
    private String URL;
    private String basic_token = "";

    // Additional header values
    private HashMap<String, String> header_hashmap = new HashMap<String, String>();

    public TravanaSpecialGetQuery(String URL){
        this.URL = URL;
    }

    public TravanaSpecialGetQuery(String URL, String key, String token){
        this.URL = URL;

        if(key != null && token != null)
            this.basic_token = Credentials.basic(key, token);
    }

    // Default onCompleteListener
    private TravanaSpecialGetQuery.OnCompleteListener onCompleteListener = (data, returnCode, success) -> {
        if (success) {
            Log.i(TAG, "Return Code: " + returnCode);
            Log.i(TAG, "Body : " + returnCode);
        } else {
            Log.e(TAG, "Failed to establish connection");
        }
    };


    public TravanaSpecialGetQuery setOnCompleteListener(@NonNull TravanaSpecialGetQuery.OnCompleteListener  onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
        return this;
    }

    /**
     * add required parameter
     * @param key value name
     * @param value the value
     * @return current instance for chaining
     */
    public TravanaSpecialGetQuery addParams(@NonNull String key, @NonNull String value) {
        if (params.length() == 0) params.append("?");
        else params.append("&");
        params.append(key).append('=').append(value);
        return this;
    }

    /**
     * add required parameter
     * @param key value name
     * @param value the value
     */
    public TravanaSpecialGetQuery addHeaderValues(@NonNull String key, @NonNull String value) {
        header_hashmap.put(key, value);
        return this;
    }

    @Override
    public void run() {
        try {

            Request.Builder builder = new Request.Builder();

            Set set = header_hashmap.entrySet();
            Iterator iterator = set.iterator();

            while(iterator.hasNext()) {
                Map.Entry mentry = (Map.Entry)iterator.next();
                builder.addHeader(mentry.getKey().toString(), mentry.getValue().toString());
            }

            builder.url(TravanaQuery.SERVER_URL + URL + params)
                    .addHeader("Content-Type", "application/json")  // add request headers
                    .addHeader("User-Agent", "OkHttp Bot")
                    .addHeader("Authorization", basic_token)
                    .addHeader("Accept","")
                    .addHeader("Cache-Control", "no-cache")
                    .addHeader("Host", TravanaQuery.SERVER_IP_ADDRESS)
                    .addHeader("Accept-Encoding", "gzip, deflate")
                    .build();

            Request request = builder.build();
            Response r = TravanaQuery.client.newCall(request).execute();

            int code = r.code();

            if(code != 200){
                onCompleteListener.onComplete(null, code, false);
            }else{

                InputStream stream = r.body().byteStream();

                onCompleteListener.onComplete(stream, code, true);
            }

        } catch (HttpStatusException e) {
            e.printStackTrace();
            onCompleteListener.onComplete(null, e.getStatusCode(), false);
        } catch (IOException e) {
            e.printStackTrace();
            onCompleteListener.onComplete(null, -1, false);
        }
    }

    /*
    @Override
    protected String doInBackground(String... apis) {
        try {

            Request.Builder builder = new Request.Builder();

            Set set = header_hashmap.entrySet();
            Iterator iterator = set.iterator();

            while(iterator.hasNext()) {
                Map.Entry mentry = (Map.Entry)iterator.next();
                builder.addHeader(mentry.getKey().toString(), mentry.getValue().toString());
            }

            builder.url(TravanaQuery.SERVER_URL + URL + params)
                    .addHeader("Content-Type", "application/json")  // add request headers
                    .addHeader("User-Agent", "OkHttp Bot")
                    .addHeader("Authorization", basic_token)
                    .addHeader("Accept","")
                    .addHeader("Cache-Control", "no-cache")
                    .addHeader("Host", TravanaQuery.SERVER_IP_ADDRESS)
                    .addHeader("Accept-Encoding", "gzip, deflate")
                    .build();

            Request request = builder.build();
            Response r = TravanaQuery.client.newCall(request).execute();

            int code = r.code();

            if(code != 200){
                onCompleteListener.onComplete(null, code, false);
                return null;
            }else{

                InputStream stream = r.body().byteStream();

                onCompleteListener.onComplete(stream, code, true);
            }

        } catch (HttpStatusException e) {
            e.printStackTrace();
            onCompleteListener.onComplete(null, e.getStatusCode(), false);
        } catch (IOException e) {
            e.printStackTrace();
            onCompleteListener.onComplete(null, -1, false);
        }
        return null;
    }

     */

    public interface OnCompleteListener {

        /**
         * Executed when HTML GET completed
         * @param inputStreamResponse String representing response body.
         * @param statusCode int equivalent to HTML status code. "-1" means IOException
         * @param success boolean if connection was successful.
         */
        void onComplete(InputStream inputStreamResponse, int statusCode, boolean success);
    }
}
