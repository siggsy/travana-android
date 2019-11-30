package com.VegaSolutions.lpptransit.travanaserver.Objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class LiveUpdateMessage {

    private String _id;
    private String user_id;
    private UserData user;
    private String[] photo_ids;
    private String created_time;
    private String message_content;
    private MessageTag[] tags;
    private int importance;									// 10 - it is shown on the top of the list.
    private int likes;
    private int comments_int;
    private boolean liked;
    public List<LiveUpdateComment> comments = new ArrayList<LiveUpdateComment>();
    private String time_ago;
    private long time_ago_millis;

    public LiveUpdateMessage(String user_id,
                             String message_content, MessageTag[] tags, String[] photo_ids) {

        this.user_id = user_id;;
        this.message_content = message_content;
        this.tags = tags;
        this.photo_ids = photo_ids;

    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getMessage_content() {
        return message_content;
    }

    public void setMessage_content(String message_content) {
        this.message_content = message_content;
    }

    public MessageTag[] getTags() {
        return tags;
    }

    public void setTags(MessageTag[] tags) {
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

    public UserData getUser() {
        return user;
    }

    public void setUser(UserData user) {
        this.user = user;
    }

    public int getComments_int() {
        return comments_int;
    }

    public void setComments_int(int comments_int) {
        this.comments_int = comments_int;
    }

    public String[] getPhoto_ids() {
        return photo_ids;
    }

    public void setPhoto_ids(String[] photo_ids) {
        this.photo_ids = photo_ids;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

    public String getTime_ago() {
        return time_ago;
    }

    public void setTime_ago(String time_ago) {
        this.time_ago = time_ago;
    }

    public long getTime_ago_millis() {
        return time_ago_millis;
    }

    public void setTime_ago_millis(long time_ago_millis) {
        this.time_ago_millis = time_ago_millis;
    }

    @Override
    public String toString() {
        return "LiveUpdateMessage{" +
                "_id='" + _id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", user=" + user +
                ", photo_ids=" + Arrays.toString(photo_ids) +
                ", created_time='" + created_time + '\'' +
                ", message_content='" + message_content + '\'' +
                ", tags=" + Arrays.toString(tags) +
                ", importance=" + importance +
                ", likes=" + likes +
                ", comments_int=" + comments_int +
                ", liked=" + liked +
                ", comments=" + comments +
                ", time_ago='" + time_ago + '\'' +
                ", time_ago_millis=" + time_ago_millis +
                '}';
    }

}


