package com.VegaSolutions.lpptransit.travanaserver.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.material.internal.ParcelableSparseArray;

public class UserTag implements Parcelable {

    private String _id;
    private String tag;
    private String color;

    private String description_slo = "";
    private String description_ang = "";

    private String followed;

    protected UserTag(Parcel in) {
        _id = in.readString();
        tag = in.readString();
        color = in.readString();
        description_slo = in.readString();
        description_ang = in.readString();
    }

    public static final Creator<UserTag> CREATOR = new Creator<UserTag>() {
        @Override
        public UserTag createFromParcel(Parcel in) {
            return new UserTag(in);
        }

        @Override
        public UserTag[] newArray(int size) {
            return new UserTag[size];
        }
    };

    public String get_id() {
        return _id;
    }

    public String getTag() {
        return tag;
    }

    public String getColor() {
        return color;
    }

    public String getDescription_slo() {
        return description_slo;
    }

    public String getDescription_ang() {
        return description_ang;
    }

    @Override
    public String toString() {
        return "UserTag{" +
                "_id='" + _id + '\'' +
                ", tag='" + tag + '\'' +
                ", color='" + color + '\'' +
                ", description_slo='" + description_slo + '\'' +
                ", description_ang='" + description_ang + '\'' +
                ", followed='" + followed + '\'' +
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
        dest.writeString(description_slo);
        dest.writeString(description_ang);
    }
}
