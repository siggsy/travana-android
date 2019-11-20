package com.VegaSolutions.lpptransit.travanaserver.Objects;

import android.os.Parcel;
import android.os.Parcelable;

public class MessageTag implements Parcelable {

    private String _id;
    private String tag;
    private String color;


    public MessageTag(String _id, String tag, String color) {
        this._id = _id;
        this.tag = tag;
        this.color = color;
    }

    protected MessageTag(Parcel in) {
        _id = in.readString();
        tag = in.readString();
        color = in.readString();
    }

    public static final Creator<MessageTag> CREATOR = new Creator<MessageTag>() {
        @Override
        public MessageTag createFromParcel(Parcel in) {
            return new MessageTag(in);
        }

        @Override
        public MessageTag[] newArray(int size) {
            return new MessageTag[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(tag);
        dest.writeString(color);
    }
}
