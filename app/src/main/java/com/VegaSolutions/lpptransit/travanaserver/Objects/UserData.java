package com.VegaSolutions.lpptransit.travanaserver.Objects;

public class UserData {

    private String _id;
    private String name;
    private String user_photo_url;
    private UserTag tag;

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

    public String getUser_photo_url() {
        return user_photo_url;
    }

    public void setUser_photo_url(String user_photo_url) {
        this.user_photo_url = user_photo_url;
    }

    public UserTag getTag() {
        return tag;
    }

    public void setTag(UserTag tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "_id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", user_photo_url='" + user_photo_url + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }

}
