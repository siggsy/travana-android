package com.VegaSolutions.lpptransit;

import android.app.Application;

import com.VegaSolutions.lpptransit.utility.NetworkConnectivityManager;

public class TravanaApp extends Application {

    public static final String TAG = "TravanaApp";
    private static TravanaApp instance;

    private NetworkConnectivityManager networkConnectivityManager;

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
}
