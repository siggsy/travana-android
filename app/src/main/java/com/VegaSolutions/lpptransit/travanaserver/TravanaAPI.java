package com.VegaSolutions.lpptransit.travanaserver;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

import com.VegaSolutions.lpptransit.lppapi.responseobjects.ApiResponse;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Bus;
import com.VegaSolutions.lpptransit.travanaserver.Objects.CalBusInfo;
import com.VegaSolutions.lpptransit.travanaserver.Objects.LiveUpdateComment;
import com.VegaSolutions.lpptransit.travanaserver.Objects.LiveUpdateMessage;
import com.VegaSolutions.lpptransit.travanaserver.Objects.TagsBox;
import com.VegaSolutions.lpptransit.travanaserver.Objects.Update;
import com.VegaSolutions.lpptransit.travanaserver.Objects.Warning;
import com.VegaSolutions.lpptransit.travanaserver.Objects.responses.ResponseObject;
import com.VegaSolutions.lpptransit.travanaserver.Objects.responses.ResponseObjectCommit;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class TravanaAPI {

    private static String TAG = "TravanaAPI";
    private static String TRAVANA_API_KEY = "mlrX6m18wsmb8UF9dQd0wcYxhE47UyYc";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /*
    *Responses
    * false, 4007, "Error: message is too long."
    * false, 4006, "Error: message is too short.
    * false, 4002, 'Error: posting too often.            -> if user has more than 3 posts in last 24 hours OR the last massage has been posted less than 10 min ago !(now - for testing it is set to 1 min)!
    * false, 4001, "Error: cannot add message
    * true, 200
    */
    public static void addMessage(String token, LiveUpdateMessage message, TravanaApiCallback<ResponseObjectCommit> callback) {

        RequestBody rbody = RequestBody.create(JSON, new Gson().toJson(message));

        new TravanaPOSTQuery(TravanaPOSTQuery.MESSAGES_ADD, TRAVANA_API_KEY, token, rbody)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {

                        try{
                            ResponseObjectCommit r = new Gson().fromJson(response, ResponseObjectCommit.class);
                            callback.onComplete(r, statusCode, true);
                        }catch (Exception e){
                            callback.onComplete(new ResponseObjectCommit(false, -3, "Error: during parsing response to object."), statusCode, false);
                        }
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).start();
    }

    /*
     *Responses
     * false, 4003, "Error: cannot find message with this _id
     * false, 3003, "Error: database down -> almost impossible
     * false, 3002, "Error: message with this id do not exists
     * true, 200
     */
    public static void removeMessage(String token, String mess_id, TravanaApiCallback<ResponseObjectCommit> callback) {

        new TravanaQuery(TravanaQuery.MESSAGES_REMOVE, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {

                        try {
                            ResponseObjectCommit r = new Gson().fromJson(response, ResponseObjectCommit.class);
                            callback.onComplete(r, statusCode, true);
                        }catch (Exception e){
                            callback.onComplete(new ResponseObjectCommit(false, -3, "Error: during parsing response to object."), statusCode, false);
                        }
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .addHeaderValues("_id", mess_id)
                .start();

    }


    /*
     *Responses
     * false, 1005, "Error: messages is already liked
     * false, 1006, "Error: messages is already unliked
     * true, 200
     */
    public static void messagesLike(String token, String mess_id, boolean like, TravanaApiCallback<ResponseObjectCommit> callback) {

        new TravanaQuery(TravanaQuery.MESSAGE_LIKE, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        try {
                            ResponseObjectCommit r = new Gson().fromJson(response, ResponseObjectCommit.class);
                            callback.onComplete(r, statusCode, true);
                        }catch (Exception e){
                            callback.onComplete(new ResponseObjectCommit(false, -3, "Error: during parsing response to object."), statusCode, false);
                        }
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).addHeaderValues("mess_id", mess_id)
                .addHeaderValues("liked", like + "")
                .start();
    }

    /*
     *Responses
     * false, 4004, "Error: comment is too long."
     * false, 4003, "Error: comment is too short."
     * false, x, "Error: posting too much (15 min limit)" -> todo, not implemented yet
     * false, 4001, "Error: during inserting comment into database
     * false, 4002, "Error: cannot find meesage with this _id
     * true, 200
     */
    public static void addComment(String token, String message_id, LiveUpdateComment comment, TravanaApiCallback<ResponseObjectCommit> callback) {

        RequestBody rbody = RequestBody.create(JSON, new Gson().toJson(comment));

        new TravanaPOSTQuery(TravanaPOSTQuery.MESSAGES_ADD_COMMENT, TRAVANA_API_KEY, token, rbody)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {

                        try {
                            ResponseObjectCommit r = new Gson().fromJson(response, ResponseObjectCommit.class);
                            callback.onComplete(r, statusCode, true);
                        }catch (Exception e){
                            callback.onComplete(new ResponseObjectCommit(false, -3, "Error: during parsing response to object."), statusCode, false);
                        }
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).addHeaderValues("_id", message_id)
                .start();
    }

    /*
     *Responses
     * false, 4001, "Error: cannot delete comment
     * false, 1004, "Error: comment with this id do not exists
     * true, 200
     */
    public static void removeComment(String token, String comment_id, TravanaApiCallback<ResponseObjectCommit> callback) {

        new TravanaQuery(TravanaQuery.MESSAGES_REMOVE_COMMENT, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {

                        try {
                            ResponseObjectCommit r = new Gson().fromJson(response, ResponseObjectCommit.class);
                            callback.onComplete(r, statusCode, true);
                        }catch (Exception e){
                            callback.onComplete(new ResponseObjectCommit(false, -3, "Error: during parsing response to object."), statusCode, false);
                        }
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .addHeaderValues("comment_id", comment_id)
                .start();
    }


    /*
     *Responses
     * false, 1005, "Error: comment is already liked
     * false, 1006, "Error: comment is already unliked
     * false, 4001, "Error: cannot unlike comment"
     * true, 200
     */
    public static void likeComment(String token, String comment_id, boolean liked, TravanaApiCallback<ResponseObjectCommit> callback) {

        new TravanaQuery(TravanaQuery.MESSAGE_COMMENT_LIKE, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {

                        try {
                            ResponseObjectCommit r = new Gson().fromJson(response, ResponseObjectCommit.class);
                            callback.onComplete(r, statusCode, true);
                        }catch (Exception e){
                            callback.onComplete(new ResponseObjectCommit(false, -3, "Error: during parsing response to object."), statusCode, false);
                        }
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .addHeaderValues("comm_id", comment_id)
                .addHeaderValues("liked", liked + "")
                .start();
    }

    /*
     *Responses
     * false, x, too often (spamming) -> todo
     * false, 4001, "Error: cannot add subcomment
     * false, 4004, "Error: cannot find comment with this id.
     * true, 200
     */
    public static void addCommentComment(String token, String comment_id, LiveUpdateComment comment, TravanaApiCallback<ResponseObjectCommit> callback) {

        RequestBody rbody = RequestBody.create(JSON, new Gson().toJson(comment));

        new TravanaPOSTQuery(TravanaPOSTQuery.MESSAGES_ADD_COMMENT_COMMENT, TRAVANA_API_KEY, token, rbody)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {

                        try {
                            ResponseObjectCommit r = new Gson().fromJson(response, ResponseObjectCommit.class);
                            callback.onComplete(r, statusCode, true);
                        }catch (Exception e){
                            callback.onComplete(new ResponseObjectCommit(false, -3, "Error: during parsing response to object."), statusCode, false);
                        }
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .addHeaderValues("comm_id", comment_id)
                .start();
    }

    /*
     *Responses
     * false, 4002, "Error: cannot find subcomment with this id.
     * false, 4001, "Error: cannot remove subcomment
     * true, 200
     */
    public static void removeCommentComment(String token, String subcomment_id, TravanaApiCallback<ResponseObjectCommit> callback) {

        new TravanaQuery(TravanaQuery.MESSAGES_REMOVE_COMMENT_COMMENT, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {

                        try {
                            ResponseObjectCommit r = new Gson().fromJson(response, ResponseObjectCommit.class);
                            callback.onComplete(r, statusCode, true);
                        }catch (Exception e){
                            callback.onComplete(new ResponseObjectCommit(false, -3, "Error: during parsing response to object."), statusCode, false);
                        }
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .addHeaderValues("subcomm_id", subcomment_id)
                .start();
    }

    /*
     *Responses
     * false, 1004, "Error: subcomment is already liked
     * false, 1005, "Error: subcomment is already unliked
     * false, 1006, "Error: cannot find subcomment with this _id
     * true, 200
     */
    public static void likeCommentComment(String token, String subcomment_id, boolean liked, TravanaApiCallback<ResponseObjectCommit> callback) {

        new TravanaQuery(TravanaQuery.MESSAGES_LIKE_COMMENT_COMMENT, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {

                        try {
                            ResponseObjectCommit r = new Gson().fromJson(response, ResponseObjectCommit.class);
                            callback.onComplete(r, statusCode, true);
                        }catch (Exception e){
                            callback.onComplete(new ResponseObjectCommit(false, -3, "Error: during parsing response to object."), statusCode, false);
                        }
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .addHeaderValues("subcomm_id", subcomment_id)
                .addHeaderValues("liked", liked + "")
                .start();
    }

    /*
     *Responses
     * false, 1004, "Error: tag is already followed
     * false, 4001, "Error: cannot follow tag
     * true, 200
     */
    public static void followTag(String token, String tag_id, TravanaApiCallback<ResponseObjectCommit> callback) {

        new TravanaQuery(TravanaQuery.FOLLOW_TAG, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {

                    Log.i("FollowTag", response);
                    if (success) {
                        try {
                            ResponseObjectCommit r = new Gson().fromJson(response, ResponseObjectCommit.class);
                            callback.onComplete(r, statusCode, true);
                        }catch (Exception e){
                            callback.onComplete(new ResponseObjectCommit(false, -3, "Error: during parsing response to object."), statusCode, false);
                        }
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).addHeaderValues("tag_id", tag_id)
                .start();
    }

    /*
     *Responses
     * false, 1005, "Error: tag is already unfollowed
     * false, 4001, "Error: cannot follow tag
     * true, 200
     */
    public static void removeTag(String token, String tag_id, TravanaApiCallback<ResponseObjectCommit> callback) {

        new TravanaQuery(TravanaQuery.REMOVE_FOLLOW_TAG, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        try {
                            ResponseObjectCommit r = new Gson().fromJson(response, ResponseObjectCommit.class);
                            callback.onComplete(r, statusCode, true);
                        }catch (Exception e){
                            callback.onComplete(new ResponseObjectCommit(false, -3, "Error: during parsing response to object."), statusCode, false);
                        }
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).addHeaderValues("tag_id", tag_id)
                . start();
    }


    //-----------------------DO REQUESTS------------------------------------------------------------

    /*
     *Responses
     * empty data or unsuccessfull callback
     * true, 200
     */
    public static void warnings(TravanaApiCallback<ResponseObject<Warning[]>> callback) {

        new TravanaQuery(TravanaQuery.WARNINGS_URL)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {

                        try {

                            ResponseObject<Warning[]> r = new Gson().fromJson(response, ResponseObject.class);

                            callback.onComplete(r, statusCode, true);

                        }catch (Exception e){
                            callback.onComplete(new ResponseObject(false, null, -3, "Error: during parsing response to object."), statusCode, false);
                        }
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).start();
    }

    /*
     *Responses
     * empty data or unsuccessfull callback
     * true, 200
     */
    public static void updates(TravanaApiCallback<ResponseObject<Update>> callback) {

        new TravanaQuery(TravanaQuery.UPDATES_URL)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {

                            try {

                                ResponseObject<Update> r = new Gson().fromJson(response, ResponseObject.class);

                                callback.onComplete(r, statusCode, true);

                            }catch (Exception e){
                                callback.onComplete(new ResponseObject(false, null, -3, "Error: during parsing response to object."), statusCode, false);
                            }

                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).start();
    }

    //First call messagesMeta and then messagesid when user clicks on message.
    @Deprecated
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

    //First call messagesMeta and then messagesid when user clicks on message.
    @Deprecated
    public static void messages(String token, TravanaApiCallback<LiveUpdateMessage[]> callback) {                                  //can be null

        new TravanaQuery(TravanaQuery.MESSAGES, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        LiveUpdateMessage[] messages = new Gson().fromJson(response, LiveUpdateMessage[].class);
                        callback.onComplete(messages, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                //.addHeaderValues("user_id", user_id)
                .start();
    }

    /*
     *Responses
     * false, 1001, "Error: during loading messages.
     * true, 200, data
     */
    public static void messagesMeta(TravanaApiCallback<ResponseObject<LiveUpdateMessage[]>> callback) {

        new TravanaQuery(TravanaQuery.MESSAGES_META)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {

                        try {

                            ResponseObject<LiveUpdateMessage[]> r = new Gson().fromJson(response, new TypeToken<ResponseObject<LiveUpdateMessage[]>>(){}.getType());
                            callback.onComplete(r, statusCode, true);

                        }catch (Exception e){
                            callback.onComplete(new ResponseObject(false, null, -3, "Error: during parsing response to object."), statusCode, false);
                        }

                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).start();
    }

    /*
     *Responses
     * false, 1001, "Error: during loading messages.
     * true, 200, data
     */
    public static void messagesMeta(String token, TravanaApiCallback<ResponseObject<LiveUpdateMessage[]>> callback) {

        new TravanaQuery(TravanaQuery.MESSAGES_META, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {

                        try {

                            ResponseObject<LiveUpdateMessage[]> r = new Gson().fromJson(response, new TypeToken<ResponseObject<LiveUpdateMessage[]>>(){}.getType());
                            callback.onComplete(r, statusCode, true);

                        }catch (Exception e){
                            callback.onComplete(new ResponseObject(false, null, -3, "Error: during parsing response to object."), statusCode, false);
                        }

                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                //.addHeaderValues("user_id", user_id)
                .start();
    }

    /*
     *Responses
     * false, 1002, "Error: message with this id do not exists
     * true, 200, data
     */
    public static void messageid(String message_id, TravanaApiCallback<ResponseObject<LiveUpdateMessage>> callback) {                                  //can be null

        new TravanaQuery(TravanaQuery.MESSAGES_ID)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {

                        try {

                            ResponseObject<LiveUpdateMessage> r = new Gson().fromJson(response, new TypeToken<ResponseObject<LiveUpdateMessage>>(){}.getType());
                            callback.onComplete(r, statusCode, true);

                        }catch (Exception e){
                            callback.onComplete(new ResponseObject(false, null, -3, "Error: during parsing response to object."), statusCode, false);
                        }

                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .addHeaderValues("mess_id", message_id)
                .start();
    }

    /*
     *Responses
     * false, 1002, "Error: message with this id do not exists
     * true, 200, data
     */
    public static void messageid(String message_id, String token, TravanaApiCallback<ResponseObject<LiveUpdateMessage>> callback) {                                  //can be null

        new TravanaQuery(TravanaQuery.MESSAGES_ID, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {

                        try {

                            ResponseObject<LiveUpdateMessage> r = new Gson().fromJson(response, new TypeToken<ResponseObject<LiveUpdateMessage>>(){}.getType());
                            callback.onComplete(r, statusCode, true);

                        }catch (Exception e){
                            callback.onComplete(new ResponseObject(false, null, -3, "Error: during parsing response to object."), statusCode, false);
                        }

                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .addHeaderValues("mess_id", message_id)
                .start();
    }


    /*
     *Responses
     * false, 1001, "Error: during loading messages.
     * true, 200, data
     */
    public static void followedMessagesMeta(String token, TravanaApiCallback<ResponseObject<LiveUpdateMessage[]>> callback) {

        new TravanaQuery(TravanaQuery.MESSAGES_FOLLOWED_META, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {

                        try {

                            ResponseObject<LiveUpdateMessage[]> r = new Gson().fromJson(response, new TypeToken<ResponseObject<LiveUpdateMessage[]>>(){}.getType());
                            callback.onComplete(r, statusCode, true);

                        }catch (Exception e){
                            callback.onComplete(new ResponseObject(false, null, -3, "Error: during parsing response to object."), statusCode, false);
                        }

                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })

                .start();
    }

    /*
     *Responses
     * false, 1001, "Error: during loading messages.
     * true, 200, data
     */
    public static void messagesByTagMeta(String tag_id, TravanaApiCallback<ResponseObject<LiveUpdateMessage[]>> callback) {

        new TravanaQuery(TravanaQuery.MESSAGES_BY_TAG_META)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {

                        try {

                            ResponseObject<LiveUpdateMessage[]> r = new Gson().fromJson(response, new TypeToken<ResponseObject<LiveUpdateMessage[]>>(){}.getType());
                            callback.onComplete(r, statusCode, true);

                        }catch (Exception e){
                            callback.onComplete(new ResponseObject(false, null, -3, "Error: during parsing response to object."), statusCode, false);
                        }

                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .addHeaderValues("tag_id", tag_id)
                .start();
    }

    /*
     *Responses
     * false, 1001, "Error: during loading messages.
     * true, 200, data
     */
    public static void messagesByTagMeta(String token, String tag_id, TravanaApiCallback<ResponseObject<LiveUpdateMessage[]>> callback) {

        new TravanaQuery(TravanaQuery.MESSAGES_BY_TAG_META, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {

                        try {

                            ResponseObject<LiveUpdateMessage[]> r = new Gson().fromJson(response, new TypeToken<ResponseObject<LiveUpdateMessage[]>>(){}.getType());
                            callback.onComplete(r, statusCode, true);

                        }catch (Exception e){
                            callback.onComplete(new ResponseObject(false, null, -3, "Error: during parsing response to object."), statusCode, false);
                        }

                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .addHeaderValues("tag_id", tag_id)
                .start();
    }

    /*
     *Responses
     * true, 200, data (can be null);
     */
    public static void tags(TravanaApiCallback<ResponseObject<TagsBox>> callback) {

        new TravanaQuery(TravanaQuery.MESSAGE_TAGS)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {

                        try {

                            ResponseObject<TagsBox> r = new Gson().fromJson(response, new TypeToken<ResponseObject<TagsBox>>(){}.getType());
                            callback.onComplete(r, statusCode, true);

                        }catch (Exception e){
                            callback.onComplete(new ResponseObject(false, null, -3, "Error: during parsing response to object."), statusCode, false);
                        }

                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .start();
    }

    /*
     *Responses
     * true, 200, data (can be null);
     */
    public static void tags(String user_id, TravanaApiCallback<ResponseObject<TagsBox>> callback) {

        new TravanaQuery(TravanaQuery.MESSAGE_TAGS)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {

                        try {

                            ResponseObject<TagsBox> r = new Gson().fromJson(response, new TypeToken<ResponseObject<TagsBox>>(){}.getType());
                            callback.onComplete(r, statusCode, true);

                        }catch (Exception e){
                            callback.onComplete(new ResponseObject(false, null, -3, "Error: during parsing response to object."), statusCode, false);
                        }

                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .addHeaderValues("user_id", user_id)
                .start();
    }

    /*
     *Responses
     * false, to often (spamming) - todo
     * false, 6000, Error: during scanning file.
     * false, 6001, Error: File content is suspicius
     * false, 6003, Error: Wrong format. We support just jpg,jpeg and png.
     * false, 6004, Error: during compressing file. Maybe you Should check if you provided file type.
     * false, 6005, Error: The system can not save the file."
     * false, 6006, "Please pass file_type and data"
     * false, 6007, "Error: posting too often."             -> you have uploaded more than 10 photos in last 10 hours.
     * true, 200, photo_id;
     */
    public static void uploadImage(String token, Uri image,Context context, TravanaApiCallback<ResponseObject<String>> callback) {

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

        Log.e(TAG, type);

        RequestBody rbody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", "image",
                        RequestBody.create(MediaType.parse("image/" + type), bytes))
                .build();

        new TravanaPOSTQuery(TravanaPOSTQuery.MESSAGES_UPLOAD_FILE, TRAVANA_API_KEY, token, rbody)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {

                        try {

                            ResponseObject r = new Gson().fromJson(response, ResponseObject.class);
                            callback.onComplete(r, statusCode, true);

                        }catch (Exception e){
                            callback.onComplete(new ResponseObject(false, null, -3, "Error: during parsing response to object."), statusCode, false);
                        }
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .addHeaderValues("file_type", type)
                .start();
    }

    /*
     *Responses
     * if everything was sucessfull just image is returned otherwise image is null or callback is unsuccessfull;
     */
    public static void getImage(String photo_id, TravanaApiCallbackSpecial callback) {

        new TravanaSpecialGetQuery(TravanaQuery.GET_IMAGE)
                .setOnCompleteListener((inputStreamResponse, statusCode, success) -> {

                    if (success) {

                        try{

                            Bitmap bitmap = BitmapFactory.decodeStream(inputStreamResponse);

                            callback.onComplete(bitmap, statusCode, true);

                        }catch (Exception e){
                            callback.onComplete(null, statusCode, false);
                        }
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .addHeaderValues("file_id", photo_id)
                .start();

    }

    /*
     *Responses
     * true, 200, data (data can be empty);
     */
    public static void calculatedBusesInfo(List<String> bus_unit_ids, TravanaApiCallback<ResponseObject<CalBusInfo[]>> callback) {

        RequestBody rbody = RequestBody.create(JSON, new Gson().toJson(bus_unit_ids));

        new TravanaPOSTQuery(TravanaPOSTQuery.BUS_CAL_INFO_IDS, rbody)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {

                        try {

                            ResponseObject<CalBusInfo[]> r = new Gson().fromJson(response, ResponseObject.class);

                            callback.onComplete(r, statusCode, true);

                        }catch (Exception e){
                            callback.onComplete(new ResponseObject(false, null, -3, "Error: during parsing response to object."), statusCode, false);
                        }

                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .start();
    }


    /*
     *Responses
     * if everything was sucessfull just image is returned otherwise image is null or callback is unsuccessfull;
     */

    private static HashMap<String, Bitmap> images = new HashMap<>();

    public static void getUserImage(@NonNull  String url, TravanaApiCallbackSpecial callbackSpecial){


        if(images.containsKey(url)){
            callbackSpecial.onComplete(images.get(url), 200, true);
            return;
        }

        Thread thread = new Thread(() -> {

            try {
                URL urlc = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlc.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);

                //images.put(url, bitmap);

                callbackSpecial.onComplete(bitmap, 200, true);

            } catch (IOException e) {
                e.printStackTrace();
                callbackSpecial.onComplete(null, -1, false);
            }

        });

        thread.start();
    }
    /*
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

        /*
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
    */
    /**
     * Executed code when query completed
     *
     * @param condition, filter messages by values (possible: "checked", "!checked")
     */
    /*
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
    */


}














