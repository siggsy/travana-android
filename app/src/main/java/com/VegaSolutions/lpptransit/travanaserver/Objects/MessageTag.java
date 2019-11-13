package com.VegaSolutions.lpptransit.travanaserver.Objects;

public class MessageTag {

    private String _id;
    private String tag;
    private String color;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "MessageTag{" +
                "_id='" + _id + '\'' +
                ", tag='" + tag + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
