package com.VegaSolutions.lpptransit.travanaserver;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import org.jsoup.HttpStatusException;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TravanaPOSTQuery extends AsyncTask<String, Void, String> {

    private static final String TAG = TravanaPOSTQuery.class.getSimpleName();

    public static OkHttpClient client = new OkHttpClient();

    public static final String SERVER_URL = "http://192.168.1.7:8081/ljubljana_app_server/api";

    //public static final String SERVER_URL = "http://192.168.1.7:8081/ljubljana_app_server/api";

    public static final String SERVER_IP_ADDRESS = "192.168.1.7:8081";

    //public static final String SERVER_IP_ADDRESS = "192.168.1.7:8081";

    public static final String ADD_USER = "/users/addUser";

    public static final String MESSAGES_APPROVAL = "/live_updates/messages/approval";

    public static final String MESSAGES_ADD = "/live_updates/messages/add";

    //TODO
    public static final String MESSAGES_EDIT = "/live_updates/messages/edit";

    public static final String MESSAGES_ADD_COMMENT = "/live_updates/messages/add_comment";

    public static final String MESSAGES_ADD_COMMENT_COMMENT = "/live_updates/messages/add_comment_comment";

    public static final String MESSAGES_EDIT_COMMENT = "/live_updates/messages/edit_comment";

    public static final String MESSAGES_FOLLOWED = "/live_updates/followed_messages";

    public static final String MESSAGES_FOLLOWED_META = "/live_updates/followed_messages_meta";

    private StringBuilder params = new StringBuilder();

    private String URL;
    private String basic_token = "";
    private RequestBody rbody;

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

    public TravanaPOSTQuery addParams(@NonNull String key, @NonNull String value) {
        if (params.length() == 0) params.append("?");
        else params.append("&");
        params.append(key).append('=').append(value);
        return this;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {

            Log.d(TAG, SERVER_URL + URL + params);

            Request request = new Request.Builder()
                    .url(SERVER_URL + URL + params)
                    .addHeader("Content-Type", "application/json")  // add request headers
                    .addHeader("User-Agent", "OkHttp Bot")
                    .addHeader("Authorization", basic_token)
                    .addHeader("Accept","")
                    .addHeader("Cache-Control", "no-cache")
                    .addHeader("Host", SERVER_IP_ADDRESS)
                    .addHeader("Accept-Encoding", "gzip, deflate")
                    .post(rbody)
                    .build();

            Response r = client.newCall(request).execute();


            String data = r.body().string();
            int code = r.code();

            if(code != 200){
                onCompleteListener.onComplete(null, code, false);
                return null;
            }else{
                onCompleteListener.onComplete(data, code, true);
            }

        }catch (HttpStatusException e){

            e.printStackTrace();
            onCompleteListener.onComplete(null, e.getStatusCode(), false);

        }catch (IOException e){

            e.printStackTrace();
            onCompleteListener.onComplete(null, -1, false);
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

