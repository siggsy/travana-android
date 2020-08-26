package com.VegaSolutions.lpptransit.lppapi.responseobjects;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Station implements Parcelable {

    private String name;
    private int int_id;
    private double latitude;
    private double longitude;
    private String ref_id;
    private List<String> route_groups_on_station;

    protected Station(Parcel in) {
        name = in.readString();
        int_id = in.readInt();
        latitude = in.readDouble();
        longitude = in.readDouble();
        ref_id = in.readString();
        route_groups_on_station = in.createStringArrayList();
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

    public int getInt_id() {
        return int_id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getRef_id() {
        return ref_id;
    }

    public List<String> getRoute_groups_on_station() {
        return route_groups_on_station;
    }

    public LatLng getLatLng() { return new LatLng(latitude, longitude); }

    public boolean isCenter() {
        return Integer.valueOf(ref_id) % 2 != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(int_id);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(ref_id);
        dest.writeStringList(route_groups_on_station);

    }

    @Override
    public String toString() {
        return "Station{" +
                "name='" + name + '\'' +
                ", int_id=" + int_id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", ref_id='" + ref_id + '\'' +
                ", route_groups_on_station=" + route_groups_on_station +
                '}';
    }
}
