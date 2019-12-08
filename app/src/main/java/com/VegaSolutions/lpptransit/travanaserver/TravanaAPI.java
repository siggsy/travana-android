package com.VegaSolutions.lpptransit.travanaserver;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.LppQuery;
import com.VegaSolutions.lpptransit.travanaserver.Objects.CalBusInfo;
import com.VegaSolutions.lpptransit.travanaserver.Objects.LiveUpdateComment;
import com.VegaSolutions.lpptransit.travanaserver.Objects.LiveUpdateMessage;
import com.VegaSolutions.lpptransit.travanaserver.Objects.MessageTag;
import com.VegaSolutions.lpptransit.travanaserver.Objects.MessagesApprovalRequest;
import com.VegaSolutions.lpptransit.travanaserver.Objects.TagsBox;
import com.VegaSolutions.lpptransit.travanaserver.Objects.Update;
import com.VegaSolutions.lpptransit.travanaserver.Objects.Warning;
import com.google.gson.Gson;

import org.jsoup.HttpStatusException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TravanaAPI {

    private static String TAG = "TravanaAPI";
    private static String TRAVANA_API_KEY = "mlrX6m18wsmb8UF9dQd0wcYxhE47UyYc";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static void warnings(TravanaApiCallback<Warning[]> callback) {

        new TravanaQuery(TravanaQuery.WARNINGS_URL)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {

                        Warning[] warnings = new Gson().fromJson(response, Warning[].class);

                        callback.onComplete(warnings, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).start();
    }

    public static void updates(TravanaApiCallback<Update> callback) {

        new TravanaQuery(TravanaQuery.UPDATES_URL)
                .setOnCompleteListener((response, statusCode, success) -> {

                    Update update = new Gson().fromJson(response, Update.class);

                    if (success) {
                        callback.onComplete(update, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).start();
    }

    public static void play_store_link(TravanaApiCallback<String> callback) {

        new TravanaQuery(TravanaQuery.PLAY_STORE_LINK)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        callback.onComplete(response, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).start();
    }


    @Deprecated
    //--> adding user-handling on server
    public static void addUser(String token, RequestBody rbody, TravanaApiCallback callback) {

        new TravanaPOSTQuery(TravanaPOSTQuery.ADD_USER, TRAVANA_API_KEY, token, rbody)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        callback.onComplete(response, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).start();
    }

    public static void messages(TravanaApiCallback<LiveUpdateMessage[]> callback) {

        new TravanaQuery(TravanaQuery.MESSAGES)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        LiveUpdateMessage[] messages = new Gson().fromJson(response, LiveUpdateMessage[].class);
                        callback.onComplete(messages, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).start();
    }

    public static void messages(String user_id, TravanaApiCallback<LiveUpdateMessage[]> callback) {                                  //can be null

        new TravanaQuery(TravanaQuery.MESSAGES)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        LiveUpdateMessage[] messages = new Gson().fromJson(response, LiveUpdateMessage[].class);
                        callback.onComplete(messages, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .addHeaderValues("user_id", user_id)
                .start();
    }

    public static void messagesMeta(TravanaApiCallback<LiveUpdateMessage[]> callback) {

        new TravanaQuery(TravanaQuery.MESSAGES_META)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        LiveUpdateMessage[] messages = new Gson().fromJson(response, LiveUpdateMessage[].class);
                        callback.onComplete(messages, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).start();
    }

    public static void messagesMeta(String user_id, TravanaApiCallback<LiveUpdateMessage[]> callback) {

        new TravanaQuery(TravanaQuery.MESSAGES_META)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        LiveUpdateMessage[] messages = new Gson().fromJson(response, LiveUpdateMessage[].class);
                        callback.onComplete(messages, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).addHeaderValues("user_id", user_id)
                .start();
    }

    public static void messageid(String message_id, TravanaApiCallback<LiveUpdateMessage> callback) {                                  //can be null

        new TravanaQuery(TravanaQuery.MESSAGES_ID)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        LiveUpdateMessage messages = new Gson().fromJson(response, LiveUpdateMessage.class);
                        callback.onComplete(messages, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .addParams("mess_id", message_id)
                .start();
    }

    public static void messageid(String message_id, String user_id, TravanaApiCallback<LiveUpdateMessage> callback) {                                  //can be null

        new TravanaQuery(TravanaQuery.MESSAGES_ID)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        LiveUpdateMessage messages = new Gson().fromJson(response, LiveUpdateMessage.class);
                        callback.onComplete(messages, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .addParams("mess_id", message_id)
                .addHeaderValues("user_id", user_id)
                .start();
    }


    //TODO -> TESTING NEEDED
    public static void messagesAdmin(String token, TravanaApiCallback callback) {

        new TravanaQuery(TravanaQuery.MESSAGES_ADMIN, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {

                        callback.onComplete(response, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).start();
    }

    //TODO -> TESTING NEEDED
    public static void messagesAdmin(String token, String message_id, TravanaApiCallback callback) {

        new TravanaQuery(TravanaQuery.MESSAGES_ADMIN, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        callback.onComplete(response, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).addParams("_id", message_id)
                .start();
    }

    /**
     * Executed code when query completed
     *
     * @param condition, filter messages by values (possible: "checked", "!checked")
     */

    //TODO -> TESTING NEEDED
    public static void messagesAdminFiltered(String token, String condition, TravanaApiCallback callback) {

        new TravanaQuery(TravanaQuery.MESSAGES_ADMIN, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        callback.onComplete(response, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).addParams("condition", condition)
                .start();
    }

    public static void addMessage(String token, LiveUpdateMessage message, TravanaApiCallback<String> callback) {

        RequestBody rbody = RequestBody.create(JSON, new Gson().toJson(message));

        new TravanaPOSTQuery(TravanaPOSTQuery.MESSAGES_ADD, TRAVANA_API_KEY, token, rbody)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        callback.onComplete(response, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).start();
    }



    //@Deprecated
    /*
    public static void editMessage(String token, String ms_id, LiveUpdateMessage message, TravanaApiCallback callback){

        RequestBody rbody = RequestBody.create(JSON, new Gson().toJson(message));

        new TravanaPOSTQuery(TravanaPOSTQuery.MESSAGES_EDIT, TRAVANA_API_KEY, token, rbody)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        callback.onComplete(response, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).addParams("_id", ms_id)
                .execute();
    }
     */

    public static void addComment(String token, String message_id, LiveUpdateComment comment, TravanaApiCallback<String> callback) {

        RequestBody rbody = RequestBody.create(JSON, new Gson().toJson(comment));

        new TravanaPOSTQuery(TravanaPOSTQuery.MESSAGES_ADD_COMMENT, TRAVANA_API_KEY, token, rbody)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        callback.onComplete(response, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).addParams("_id", message_id)
                .start();
    }

    public static void addCommentComment(String token, String comment_id, LiveUpdateComment comment, TravanaApiCallback<String> callback) {

        RequestBody rbody = RequestBody.create(JSON, new Gson().toJson(comment));

        new TravanaPOSTQuery(TravanaPOSTQuery.MESSAGES_ADD_COMMENT_COMMENT, TRAVANA_API_KEY, token, rbody)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        callback.onComplete(response, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .addParams("comm_id", comment_id)
                .start();
    }

    public static void removeMessage(String token, String mess_id, TravanaApiCallback<String> callback) {

        new TravanaQuery(TravanaQuery.MESSAGES_REMOVE, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        callback.onComplete(response, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).addParams("_id", mess_id)
                .start();

    }

    public static void removeComment(String token, String comment_id, TravanaApiCallback<String> callback) {

        new TravanaQuery(TravanaQuery.MESSAGES_REMOVE_COMMENT, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        callback.onComplete(response, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .addParams("comment_id", comment_id)
                .start();
    }

    public static void removeCommentComment(String token, String subcomment_id, TravanaApiCallback<String> callback) {

        new TravanaQuery(TravanaQuery.MESSAGES_REMOVE_COMMENT_COMMENT, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        callback.onComplete(response, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .addParams("subcomm_id", subcomment_id)
                .start();
    }

    public static void likeCommentComment(String token, String subcomment_id, boolean liked, TravanaApiCallback<String> callback) {

        new TravanaQuery(TravanaQuery.MESSAGES_LIKE_COMMENT_COMMENT, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        callback.onComplete(response, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .addParams("subcomm_id", subcomment_id)
                .addHeaderValues("liked", liked + "")
                .start();
    }

    //@Depricated
    /*
    public static void editComment(String token, String message_id, String comment_id, LiveUpdateComment comment, TravanaApiCallback callback) {

        RequestBody rbody = RequestBody.create(JSON, new Gson().toJson(comment));

        new TravanaPOSTQuery(TravanaPOSTQuery.MESSAGES_EDIT_COMMENT, TRAVANA_API_KEY, token, rbody)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        callback.onComplete(response, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).addParams("_id", message_id)
                .addParams("comment_id", comment_id)
                .execute();
    }

     */


    //TODO -> TESTING NEEDED (DEPICATED?)
    public static void approveMessages(String token, List<MessagesApprovalRequest> approvalRequests, TravanaApiCallback callback) {

        RequestBody rbody = RequestBody.create(JSON, new Gson().toJson(approvalRequests));

        new TravanaPOSTQuery(TravanaPOSTQuery.MESSAGES_APPROVAL, TRAVANA_API_KEY, token, rbody)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        callback.onComplete(response, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .start();
    }


    public static void banUser(String token, String user_id, TravanaApiCallback<String> callback) {

        new TravanaQuery(TravanaQuery.BAN_USER, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        callback.onComplete(response, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).addParams("_id", user_id)
                .start();
    }

    public static void messagesLike(String token, String mess_id, boolean like, TravanaApiCallback<String> callback) {

        new TravanaQuery(TravanaQuery.MESSAGE_LIKE, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        callback.onComplete(response, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).addHeaderValues("mess_id", mess_id)
                .addHeaderValues("liked", like + "")
                //.addHeaderValues("user_id", user_id)
                .start();
    }

    public static void messagesMarkSeen(String token, String mess_id, boolean seen, TravanaApiCallback<String> callback) {

        new TravanaQuery(TravanaQuery.MESSAGES_MARK_SEEN, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        callback.onComplete(response, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).addHeaderValues("mess_id", mess_id)
                .addHeaderValues("seen", seen + "")
                .start();
    }

    public static void messagesMarkNotified(String token, String mess_id, boolean notified, TravanaApiCallback<String> callback) {

        new TravanaQuery(TravanaQuery.MESSAGES_MARK_NOTIFIED, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        callback.onComplete(response, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).addHeaderValues("mess_id", mess_id)
                .addHeaderValues("seen", notified + "")
                .start();
    }

    public static void messagesFollowedUnseenMeta(String token, TravanaApiCallback<LiveUpdateMessage[]> callback) {

        new TravanaQuery(TravanaQuery.MESSAGES_FOLLOWD_UNSEEN_META, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {

                        LiveUpdateMessage[] messages = new Gson().fromJson(response, LiveUpdateMessage[].class);
                        callback.onComplete(messages, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .start();
    }

    public static void commentLike(String token, String comm_id, boolean like, TravanaApiCallback<String> callback) {          //like = true -> likes++ , like = false -> likes--

        new TravanaQuery(TravanaQuery.MESSAGE_COMMENT_LIKE, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        callback.onComplete(response, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .addHeaderValues("comm_id", comm_id)
                .addHeaderValues("liked", like + "")
                .start();
    }

    /*
    public static void followedMessages(String user_id, String[] tags_ids, TravanaApiCallback callback) {

        RequestBody rbody = RequestBody.create(JSON, new Gson().toJson(tags_ids));

        new TravanaPOSTQuery(TravanaPOSTQuery.MESSAGES_FOLLOWED, rbody)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        LiveUpdateMessage[] messages = new Gson().fromJson(response, LiveUpdateMessage[].class);
                        callback.onComplete(messages, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .execute();
    }

     */

    public static void followedMessagesMeta(String token, TravanaApiCallback<LiveUpdateMessage[]> callback) {

        new TravanaQuery(TravanaQuery.MESSAGES_FOLLOWED_META, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        LiveUpdateMessage[] messages = new Gson().fromJson(response, LiveUpdateMessage[].class);
                        callback.onComplete(messages, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .start();

    }

    public static void messagesByTag(String tag_id, TravanaApiCallback<LiveUpdateMessage[]> callback) {

        new TravanaQuery(TravanaQuery.MESSAGES_BY_TAG)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {

                        LiveUpdateMessage[] messages = new Gson().fromJson(response, LiveUpdateMessage[].class);

                        callback.onComplete(messages, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .addHeaderValues("tag_id", tag_id)
                .start();
    }

    public static void messagesByTag(String token, String tag_id, TravanaApiCallback<LiveUpdateMessage[]> callback) {

        new TravanaQuery(TravanaQuery.MESSAGES_BY_TAG_U, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {

                        LiveUpdateMessage[] messages = new Gson().fromJson(response, LiveUpdateMessage[].class);

                        callback.onComplete(messages, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .addHeaderValues("tag_id", tag_id)
                .start();
    }

    public static void tags(TravanaApiCallback<TagsBox> callback) {

        new TravanaQuery(TravanaQuery.MESSAGE_TAGS)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {

                        TagsBox tags = new Gson().fromJson(response, TagsBox.class);
                        callback.onComplete(tags, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .start();
    }


    public static void uploadImage(String token, byte[] bytes, String file_type, TravanaApiCallback<String> callback) {

        RequestBody rbody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", "image",
                        RequestBody.create(MediaType.parse("image/png"), bytes))
                .build();

        new TravanaPOSTQuery(TravanaPOSTQuery.MESSAGES_UPLOAD_FILE, TRAVANA_API_KEY, token, rbody)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        callback.onComplete(response, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .addHeaderValues("file_type", file_type)
                .start();
    }

    public static void uploadImage(String token, Uri image,Context context, TravanaApiCallback<String> callback) {

        byte[] bytes = null;

        try {
            bytes = Utils.getBytes(context, image);

        } catch (IOException e) {

            callback.onComplete(null, -1, false);
            return;

        }

        ContentResolver cR = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String type = mime.getExtensionFromMimeType(cR.getType(image));

        RequestBody rbody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", "image",
                        RequestBody.create(MediaType.parse("image/png"), bytes))
                .build();

        new TravanaPOSTQuery(TravanaPOSTQuery.MESSAGES_UPLOAD_FILE, TRAVANA_API_KEY, token, rbody)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        callback.onComplete(response, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .addHeaderValues("file_type", type)
                .start();
    }

    public static void getImage(String photo_id, TravanaApiCallbackSpecial callback) {

        new TravanaSpecialGetQuery(TravanaQuery.GET_IMAGE)
                .setOnCompleteListener((inputStreamResponse, statusCode, success) -> {

                    if (success) {

                        Bitmap bitmap = BitmapFactory.decodeStream(inputStreamResponse);

                        callback.onComplete(bitmap, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .addHeaderValues("file_id", photo_id)
                .start();

    }

    public static void followTag(String token, String tag_id, TravanaApiCallback<String> callback) {

        new TravanaQuery(TravanaQuery.FOLLOW_TAG, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        callback.onComplete(response, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).addHeaderValues("tag_id", tag_id)
                .start();
    }

    public static void removeTag(String token, String tag_id, TravanaApiCallback<String> callback) {

        new TravanaQuery(TravanaQuery.REMOVE_FOLLOW_TAG, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        callback.onComplete(response, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).addHeaderValues("tag_id", tag_id)
                . start();
    }

    public static void calculatedBusesInfo(List<String> bus_unit_ids, TravanaApiCallback<CalBusInfo[]> callback) {

        RequestBody rbody = RequestBody.create(JSON, new Gson().toJson(bus_unit_ids));

        new TravanaPOSTQuery(TravanaPOSTQuery.BUS_CAL_INFO_IDS, rbody)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {

                        CalBusInfo[] busInfos = new Gson().fromJson(response, CalBusInfo[].class);

                        callback.onComplete(busInfos, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .start();
    }

    public static void getUserImage(@NonNull  String url, TravanaApiCallbackSpecial callbackSpecial){

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    URL urlc = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) urlc.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(input);

                    callbackSpecial.onComplete(bitmap, 200, true);

                } catch (IOException e) {
                    e.printStackTrace();
                    callbackSpecial.onComplete(null, -1, false);
                }

            }
        });

        thread.start();

    }

}














