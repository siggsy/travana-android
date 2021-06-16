package com.VegaSolutions.lpptransit.lppapi.responseobjects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Station implements Parcelable {

    private final String name;

    @SerializedName("int_id")
    private final int intId;

    private final double latitude;
    private final double longitude;

    @SerializedName("ref_id")
    private final String refId;

    @SerializedName("route_groups_on_station")
    private final List<String> routeGroupsOnStation;

    protected Station(Parcel in) {
        name = in.readString();
        intId = in.readInt();
        latitude = in.readDouble();
        longitude = in.readDouble();
        refId = in.readString();
        routeGroupsOnStation = in.createStringArrayList();
    }

    public static final Creator<Station> CREATOR = new Creator<Station>() {
        @Override
        public Station createFromParcel(Parcel in) {
            return new Station(in);
        }

        @Override
        public Station[] newArray(int size) {
            return new Station[size];
        }
    };

    public String getName() {
        return name;
    }

    public int getIntId() {
        return intId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getRefId() {
        return refId;
    }

    public List<String> getRouteGroupsOnStation() {
        return routeGroupsOnStation;
    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    public boolean isCenter() {
        return Integer.valueOf(refId) % 2 != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(intId);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(refId);
        dest.writeStringList(routeGroupsOnStation);

    }

    @Override
    public String toString() {
        return "Station{" +
                "name='" + name + '\'' +
                ", int_id=" + intId +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", ref_id='" + refId + '\'' +
                ", route_groups_on_station=" + routeGroupsOnStation +
                '}';
    }
}
