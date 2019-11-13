package com.VegaSolutions.lpptransit.travanaserver;

import android.util.Log;

import com.VegaSolutions.lpptransit.lppapi.LppQuery;
import com.VegaSolutions.lpptransit.travanaserver.Objects.LiveUpdateComment;
import com.VegaSolutions.lpptransit.travanaserver.Objects.LiveUpdateMessage;
import com.VegaSolutions.lpptransit.travanaserver.Objects.MessagesApprovalRequest;
import com.VegaSolutions.lpptransit.travanaserver.Objects.Warning;
import com.google.gson.Gson;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class TravanaAPI {

    private static String TAG = "TravanaAPI";
    private static String TRAVANA_API_KEY = "mlrX6m18wsmb8UF9dQd0wcYxhE47UyYc";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static void warnings(TravanaApiCallback callback) {

        new TravanaQuery(TravanaQuery.WARNINGS_URL)
                .setOnCompleteListener((response, statusCode, success) -> {

            if(success){

                Gson gson = new Gson();

                Warning[] warnings = gson.fromJson(response, Warning[].class);

                callback.onComplete(warnings, statusCode, true);
            }else{
                callback.onComplete(null, statusCode, false);
            }
        }).execute();
    }

    public static void updates(TravanaApiCallback callback){

        new TravanaQuery(TravanaQuery.UPDATES_URL)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if(success){
                        callback.onComplete(response, statusCode, true);
                    }else{
                        callback.onComplete(null, statusCode, false);
                    }
                }).execute();
    }

    public static void play_store_link(TravanaApiCallback callback){

        new TravanaQuery(TravanaQuery.PLAY_STORE_LINK)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if(success){
                        callback.onComplete(response, statusCode, true);
                    }else{
                        callback.onComplete(null, statusCode, false);
                    }
                }).execute();
    }


    //depricated --> adding user-handling on server
    public static void addUser(String token, RequestBody rbody, TravanaApiCallback callback){

        new TravanaPOSTQuery(TravanaPOSTQuery.ADD_USER,TRAVANA_API_KEY, token, rbody)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if(success){
                        callback.onComplete(response, statusCode, true);
                    }else{
                        callback.onComplete(null, statusCode, false);
                    }
                }).execute();
    }

    public static void messages(TravanaApiCallback callback) {

        new TravanaQuery(TravanaQuery.MESSAGES)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        LiveUpdateMessage[] messages = new Gson().fromJson(response, LiveUpdateMessage[].class);
                        callback.onComplete(messages, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).execute();
    }

    public static void messagesAdmin(String token, TravanaApiCallback callback) {

        new TravanaQuery(TravanaQuery.MESSAGES_ADMIN, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        callback.onComplete(response, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).execute();
    }

    public static void messagesAdmin(String token, String message_id, TravanaApiCallback callback) {

        new TravanaQuery(TravanaQuery.MESSAGES_ADMIN, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        callback.onComplete(response, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).addParams("_id", message_id )
                .execute();
    }

    /**
     * Executed code when query completed
     * @param condition, filter messages by values (possible: "checked", "!checked")
     */

    public static void messagesAdminFiltered(String token, String condition, TravanaApiCallback callback) {

        new TravanaQuery(TravanaQuery.MESSAGES_ADMIN, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        callback.onComplete(response, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).addParams("condition", condition)
                .execute();
    }

    public static void addMessage(String token, LiveUpdateMessage message, TravanaApiCallback callback){

        RequestBody rbody = RequestBody.create(JSON, new Gson().toJson(message));

        new TravanaPOSTQuery(TravanaPOSTQuery.MESSAGES_ADD, TRAVANA_API_KEY, token, rbody)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        callback.onComplete(response, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).execute();
    }

    //TODO --
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

    public static void addComment(String token, String ms_id, LiveUpdateComment comment, TravanaApiCallback callback){

        RequestBody rbody = RequestBody.create(JSON, new Gson().toJson(comment));

        new TravanaPOSTQuery(TravanaPOSTQuery.MESSAGES_ADD_COMMENT, TRAVANA_API_KEY, token, rbody)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        callback.onComplete(response, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).addParams("_id", ms_id)
                .execute();
    }

    public static void removeMessage(String token, String _id, TravanaApiCallback callback){

        new TravanaQuery(TravanaQuery.MESSAGES_REMOVE, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        callback.onComplete(response, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).addParams("_id", _id)
                .execute();

    }

    public static void removeComment(String token, String message_id,String comment_id,TravanaApiCallback callback){

        new TravanaQuery(TravanaQuery.MESSAGES_REMOVE_COMMENT, TRAVANA_API_KEY, token)
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

    public static void editComment(String token, String message_id, String comment_id, LiveUpdateComment comment, TravanaApiCallback callback){

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

    public static void approveMessages(String token, List<MessagesApprovalRequest> approvalRequests, TravanaApiCallback callback){

        RequestBody rbody = RequestBody.create(JSON, new Gson().toJson(approvalRequests));

        new TravanaPOSTQuery(TravanaPOSTQuery.MESSAGES_APPROVAL, TRAVANA_API_KEY, token, rbody)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        callback.onComplete(response, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .execute();
    }


    public static void banUser(String token, String user_id, TravanaApiCallback callback){

        new TravanaQuery(TravanaQuery.BAN_USER, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        callback.onComplete(response, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).addParams("_id", user_id)
                .execute();
    }

    public static void messagesLike(String token,String mess_id, boolean like, TravanaApiCallback callback) {          //like = true -> likes++ , like = false -> likes--

        new TravanaQuery(TravanaQuery.MESSAGE_LIKE, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        callback.onComplete(response, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).addHeaderValues("mess_id", mess_id)
                .addHeaderValues("liked", like + "")
                .execute();
    }

    public static void commentLike(String token,String mess_id, String comm_id, boolean like, TravanaApiCallback callback) {          //like = true -> likes++ , like = false -> likes--

        new TravanaQuery(TravanaQuery.MESSAGE_COMMENT_LIKE, TRAVANA_API_KEY, token)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        callback.onComplete(response, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                }).addHeaderValues("mess_id", mess_id)
                .addHeaderValues("comm_id", comm_id)
                .addHeaderValues("liked", like + "")
                .execute();
    }

    public static void followedMessages(String token, String[] tags_ids, TravanaApiCallback callback){

        RequestBody rbody = RequestBody.create(JSON, new Gson().toJson(tags_ids));

        new TravanaPOSTQuery(TravanaPOSTQuery.MESSAGES_FOLLOWED, TRAVANA_API_KEY, token, rbody)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        callback.onComplete(response, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .execute();
    }

    public static void tags(TravanaApiCallback callback){

        new TravanaQuery(TravanaQuery.MESSAGE_TAGS)
                .setOnCompleteListener((response, statusCode, success) -> {

                    if (success) {
                        callback.onComplete(response, statusCode, true);
                    } else {
                        callback.onComplete(null, statusCode, false);
                    }
                })
                .execute();
    }

}


























