package com.VegaSolutions.lpptransit.travanaserver.Objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class LiveUpdateMessage {

    private String _id;
    private String user_id;
    private UserData user;
    private String[] photo_links;
    private Date created_time;
    private Date expire_date;
    private String message_content;
    private MessageTag[] tags;
    private int importance;									// 10 - it is shown on the top of the list.
    private int likes;
    private int comments_int;
    private boolean liked;
    public List<LiveUpdateComment> comments = new ArrayList<LiveUpdateComment>();

    public LiveUpdateMessage(String user_id, Date expire_date,
                             String message_content, MessageTag[] tags, String[] photo_links) {

        this.user_id = user_id;
        this.created_time = new Date();
        this.expire_date = expire_date;
        this.message_content = message_content;
        this.tags = tags;
        this.photo_links = photo_links;

        String _id = "mess_gen" + user_id + created_time;
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

    public String[] getPhoto_links() {
        return photo_links;
    }

    public void setPhoto_links(String[] photo_links) {
        this.photo_links = photo_links;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
        setLikes(liked ? likes + 1 : likes - 1);
    }

    @Override
    public String toString() {
        return "LiveUpdateMessage{" +
                "_id='" + _id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", user=" + user +
                ", created_time=" + created_time +
                ", expire_date=" + expire_date +
                ", message_content='" + message_content + '\'' +
                ", tags=" + Arrays.toString(tags) +
                ", importance=" + importance +
                ", likes=" + likes +
                ", comments_int=" + comments_int +
                ", comments=" + comments +
                '}';
    }
}
