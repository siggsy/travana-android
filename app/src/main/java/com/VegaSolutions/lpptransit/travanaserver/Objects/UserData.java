package com.VegaSolutions.lpptransit.travanaserver.Objects;

public class UserData {

    private String _id;
    private String name;
    private String email;
    private String user_photo_url;

    public UserData(String _id, String name, String email, String user_photo_url) {
        this._id = _id;
        this.name = name;
        this.email = email;
        this.user_photo_url = user_photo_url;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser_photo_url() {
        return user_photo_url;
    }

    public void setUser_photo_url(String user_photo_url) {
        this.user_photo_url = user_photo_url;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "_id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", user_photo_url='" + user_photo_url + '\'' +
                '}';
    }
}
