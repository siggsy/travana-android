package com.VegaSolutions.lpptransit.travanaserver.Objects;

import java.util.Date;

public class LiveUpdateComment {

    private String user_id;

    //private String photo_path;
    private String comment_id;
    private String comment_content;
    private String created_date;
    private int likes;
    private String time_ago;
    private long time_ago_millis;

    
    public LiveUpdateComment(String comment_content) {

        this.comment_content = comment_content;

    }

    /*
    public LiveUpdateComment(String user_id, String comment_content) {
        this.user_id = user_id;
        this.comment_content = comment_content;
        this.created_date = new Date();

        String _id = "comm_gen" + user_id+ created_date;
        _id = _id.replaceAll(" ", "");
        _id = _id.replace("+", "");

        this.comment_id = _id;
    }

     */


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getComment_content() {
        return comment_content;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }

    public String getCreated_date() {
        return created_date;
    }

    public int getLikes() {
        return likes;
    }

    public String getTime_ago() {
        return time_ago;
    }

    public long getTime_ago_millis() {
        return time_ago_millis;
    }

    @Override
    public String toString() {
        return "LiveUpdateComment{" +
                "user_id='" + user_id + '\'' +
                ", comment_id='" + comment_id + '\'' +
                ", comment_content='" + comment_content + '\'' +
                ", created_date=" + created_date +
                '}';
    }
}
