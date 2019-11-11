package com.VegaSolutions.lpptransit.travanaserver.Objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class LiveUpdateMessage {

    private String _id;
    private String user_id;
    private String name;
    private String user_photo_url;
    //private String photo_path;
    private Date created_time;
    private Date expire_date;
    private String message_content;
    private String[] tags;
    private int importance;									// 10 - it is shown on the top of the list.
    private int likes;
    public List<LiveUpdateComment> comments = new ArrayList<LiveUpdateComment>();

    //private String[] photos_paths;

    public LiveUpdateMessage(){

    }

    public LiveUpdateMessage(String user_id, String name, String user_photo_url, Date expire_date,
                             String message_content, String[] tags, int importance) {

        this.user_id = user_id;
        this.name = name;
        this.user_photo_url = user_photo_url;
        this.created_time = new Date();
        this.expire_date = expire_date;
        this.message_content = message_content;
        this.tags = tags;
        this.importance = importance;

        String _id = "mess_gen" + name + created_time;
        _id = _id.replaceAll(" ", "");
        _id = _id.replace("+", "");

        this._id = _id;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreated_time() {
        return created_time;
    }

    public void setCreated_time(Date created_time) {
        this.created_time = created_time;
    }

    public Date getExpire_date() {
        return expire_date;
    }

    public void setExpire_date(Date expire_date) {
        this.expire_date = expire_date;
    }

    public String getMessage_content() {
        return message_content;
    }

    public void setMessage_content(String message_content) {
        this.message_content = message_content;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public List<LiveUpdateComment> getComments() {
        return comments;
    }

    public void setComments(List<LiveUpdateComment> comments) {
        this.comments = comments;
    }


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_photo_url() {
        return user_photo_url;
    }

    public void setUser_photo_url(String user_photo_url) {
        this.user_photo_url = user_photo_url;
    }

    @Override
    public String toString() {
        return "LiveUpdateMessage{" +
                "_id='" + _id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", name='" + name + '\'' +
                ", user_photo_url='" + user_photo_url + '\'' +
                ", created_time=" + created_time +
                ", expire_date=" + expire_date +
                ", message_content='" + message_content + '\'' +
                ", tags=" + Arrays.toString(tags) +
                ", importance=" + importance +
                ", likes=" + likes +
                ", comments=" + comments +
                '}';
    }
}
