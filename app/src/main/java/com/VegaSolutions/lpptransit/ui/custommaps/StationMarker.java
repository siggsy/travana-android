package com.VegaSolutions.lpptransit.ui.custommaps;

import com.VegaSolutions.lpptransit.lppapi.responseobjects.Station;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class StationMarker implements ClusterItem {

    private LatLng mPosition;
    private String mTitle;
    private String mSnippet;
    private Station station;

    public StationMarker(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    public StationMarker(double lat, double lng, Station station) {
        mPosition = new LatLng(lat, lng);
        this.station = station;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }

    public Station getStation() {
        return station;
    }
}
