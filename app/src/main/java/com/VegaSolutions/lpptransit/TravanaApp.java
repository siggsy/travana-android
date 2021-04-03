package com.VegaSolutions.lpptransit;

import android.app.Application;

import com.VegaSolutions.lpptransit.lppapi.responseobjects.Station;
import com.VegaSolutions.lpptransit.utility.NetworkConnectivityManager;

import java.util.ArrayList;

public class TravanaApp extends Application {

    public static final String TAG = "TravanaApp";
    private static TravanaApp instance;

    private NetworkConnectivityManager networkConnectivityManager;
    private ArrayList<Station> stations = null;

    public static TravanaApp getInstance() {
        return TravanaApp.instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        networkConnectivityManager = new NetworkConnectivityManager(this);
    }

    public NetworkConnectivityManager getNetworkConnectivityManager() {
        return networkConnectivityManager;
    }

    public boolean areStationsLoaded() {
        return stations != null;
    }

    public ArrayList<Station> getStations() {
        return stations;
    }

    public void setStations(ArrayList<Station> stations) {
        this.stations = stations;
    }
}
