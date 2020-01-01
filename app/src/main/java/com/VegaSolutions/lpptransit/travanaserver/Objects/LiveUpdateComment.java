package com.VegaSolutions.lpptransit.travanaserver.Objects;

import java.util.Date;

public class LiveUpdateComment {

    private String user_id;

    //private String photo_path;
    private UserData user;
    private String comment_id;
    private String comment_content;
    private LiveUpdateComment[] subcomments;
    private String created_date;
    private int likes;
    private boolean liked;
    private String time_ago;
    private long time_ago_millis;

    
    public LiveUpdateComment(String comment_content) {

        this.comment_content = comment_content;

    }

    public LiveUpdateComment[] getSubcomments() {
        return subcomments;
    }

    public UserData getUser() {
        return user;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

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
