package com.VegaSolutions.lpptransit.travanaserver;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import org.jsoup.HttpStatusException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TravanaQuery extends Thread  {

    private static final String TAG = TravanaQuery.class.getSimpleName();

    public static OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS).build();

    public static final String SERVER_URL = "http://192.168.1.13:8081/ljubljana_app_server/api";

    //public static final String SERVER_URL = "https://192.168.1.13:8443/ljubljana_app_server/api";

    //public static final String SERVER_URL = "http://10.0.1.23:8080/ljubljana_app_server/api";

    //public static final String SERVER_IP_ADDRESS = "193.77.85.172:8081";

    public static final String SERVER_IP_ADDRESS = "192.168.1.13:8081";

    //public static final String SERVER_IP_ADDRESS = "192.168.1.13:8443";

    public static final String WARNINGS_URL = "/alerts/warnings";                                   //returns warnings alerts. ex. "{"content": "vsebina","created_date": "10.12.2001","expire_date": "1.1.2020",...

    public static final String UPDATES_URL = "/alerts/updates";                                     //returns needed upates and still supported verisons ex "{"lastVerison": 110,"stillSupportedVersions": 101,102,...

    public static final String PLAY_STORE_LINK = "/alerts/play_store_link";                         //returns playstore update/download link ex "https://play.google.com/store/apps/details?id=com.easistent.family"

    public static final String MESSAGES = "/live_updates/messages";

    public static final String MESSAGES_REMOVE = "/live_updates/messages/remove";

    public static final String MESSAGES_ADMIN = "/live_updates/messages_admin";

    public static final String MESSAGES_UPDATE = "/live_updates/messages/update";                   //Messages are stored in ram, in case you have changed data directly in database, call url.

    public static final String MESSAGES_REMOVE_COMMENT = "/live_updates/messages/remove_comment";

    public static final String MESSAGES_REMOVE_COMMENT_COMMENT = "/live_updates/messages/remove_comment_comment";

    public static final String MESSAGES_LIKE_COMMENT_COMMENT = "/live_updates/messages/like_comment_comment";

    public static final String BAN_USER = "/users/banUser";

    public static final String MESSAGE_LIKE = "/live_updates/like_message";

    public static final String MESSAGE_COMMENT_LIKE = "/live_updates/like_comment";

    public static final String MESSAGE_TAGS = "/live_updates/tags";

    public static final String MESSAGES_META = "/live_updates/messages_meta";

    public static final String MESSAGES_ID = "/live_updates/messagesid";

    public static final String GET_IMAGE = "/file/get";

    public static final String FOLLOW_TAG = "/live_updates/messages/add_followed_tag";

    public static final String REMOVE_FOLLOW_TAG = "/live_updates/messages/remove_followed_tag";

    public static final String MESSAGES_FOLLOWED = "/live_updates/followed_messages";

    public static final String MESSAGES_FOLLOWED_META = "/live_updates/followed_messages_meta";

    // Url parameters
    private StringBuilder params = new StringBuilder();
    private String URL;
    private String basic_token = "";

    // Additional header values
    private HashMap<String, String> header_hashmap = new HashMap<String, String>();

    public TravanaQuery(String URL){
        this.URL = URL;
    }

    public TravanaQuery(String URL, String key, String token){
        this.URL = URL;

        if(key != null && token != null)
            this.basic_token = Credentials.basic(key, token);
    }

    // Default onCompleteListener
    private TravanaQuery.OnCompleteListener onCompleteListener = (data, returnCode, success) -> {
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
    public TravanaQuery setOnCompleteListener(@NonNull OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
        return this;
    }

    /**
     * add required parameter
     * @param key value name
     * @param value the value
     * @return current instance for chaining
     */
    public TravanaQuery addParams(@NonNull String key, @NonNull String value) {
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
    public TravanaQuery addHeaderValues(@NonNull String key, @NonNull String value) {
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

            Log.d(TAG, SERVER_URL + URL + params);

            builder.url(SERVER_URL + URL + params)
                    .addHeader("Content-Type", "application/json")  // add request headers
                    .addHeader("User-Agent", "OkHttp Bot")
                    .addHeader("Authorization", basic_token)
                    .addHeader("Accept","")
                    .addHeader("Cache-Control", "no-cache")
                    .addHeader("Host", SERVER_IP_ADDRESS)
                    .addHeader("Accept-Encoding", "gzip, deflate")
                    .build();

            Request request = builder.build();
            Response r = client.newCall(request).execute();


            String data = r.body().string();

            int code = r.code();

            if(code != 200){
                onCompleteListener.onComplete(null, code, false);
            }else{
                onCompleteListener.onComplete(data, code, true);
            }

        } catch (HttpStatusException e) {
            e.printStackTrace();
            onCompleteListener.onComplete(null, e.getStatusCode(), false);
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            onCompleteListener.onComplete(null, -2, false);
        } catch (IOException e) {
            e.printStackTrace();
            onCompleteListener.onComplete(null, -1, false);
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
