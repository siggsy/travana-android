package com.VegaSolutions.lpptransit.travanaserver;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import org.jsoup.HttpStatusException;

import java.io.File;
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
import okhttp3.RequestBody;
import okhttp3.Response;

public class TravanaPOSTQuery extends Thread {

    private static final String TAG = TravanaPOSTQuery.class.getSimpleName();

    public static OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS).build();

    //public static final String SERVER_URL = "http://193.77.85.172:8081/ljubljana_app_server/api";

    public static final String SERVER_URL = "http://193.77.85.172:8081/ljubljana_app_server/api";

   // public static final String SERVER_URL = "http://192.168.1.7:8081/ljubljana_app_server/api";

    public static final String SERVER_IP_ADDRESS = "193.77.85.172:8081";

    //public static final String SERVER_IP_ADDRESS = "192.168.1.7:8081";

    public static final String ADD_USER = "/users/addUser";

    public static final String MESSAGES_APPROVAL = "/live_updates/messages/approval";

    public static final String MESSAGES_ADD = "/live_updates/messages/add";

    //TODO
    public static final String MESSAGES_EDIT = "/live_updates/messages/edit";

    public static final String MESSAGES_ADD_COMMENT = "/live_updates/messages/add_comment";

    public static final String MESSAGES_ADD_COMMENT_COMMENT = "/live_updates/messages/add_comment_comment";

    public static final String MESSAGES_EDIT_COMMENT = "/live_updates/messages/edit_comment";

    public static final String MESSAGES_UPLOAD_FILE = "/file/upload";

    public static final String BUS_CAL_INFO_IDS = "/lpp_buses/calculated_traffic/ids";

    private StringBuilder params = new StringBuilder();

    private String URL;
    private String basic_token = "";
    private RequestBody rbody;

    private HashMap<String, String> header_hashmap = new HashMap<String, String>();

    public TravanaPOSTQuery(String URL, String key, String token, RequestBody rbody){
        this.URL = URL;

        if(key != null && token != null)
            this.basic_token = Credentials.basic(key, token);

        this.rbody = rbody;

    }

    public TravanaPOSTQuery(String URL, RequestBody rbody){
        this.URL = URL;

        this.rbody = rbody;

    }

    // Default onCompleteListener
    private TravanaPOSTQuery.OnCompleteListener onCompleteListener = (data, returnCode, success) -> {
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
    public TravanaPOSTQuery setOnCompleteListener(@NonNull OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
        return this;
    }

    /**
     * add required parameter
     * @param key value name
     * @param value the value
     */
    public TravanaPOSTQuery addHeaderValues(@NonNull String key, @NonNull String value) {
        header_hashmap.put(key, value);
        return this;
    }

    /**
     * add required parameter
     * @param key value name
     * @param value the value
     * @return current instance for chaining
     */
    public TravanaPOSTQuery addParams(@NonNull String key, @NonNull String value) {
        if (params.length() == 0) params.append("?");
        else params.append("&");
        params.append(key).append('=').append(value);
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
                    .post(rbody)
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

        }catch (HttpStatusException e){

            e.printStackTrace();
            onCompleteListener.onComplete(null, e.getStatusCode(), false);

        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            onCompleteListener.onComplete(null, -2, false);
        }catch (IOException e){

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

    public static RequestBody getRequestBodyFile(File file){
        return null;
    }
}

