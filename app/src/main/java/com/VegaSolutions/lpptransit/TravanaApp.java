package com.VegaSolutions.lpptransit;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.VegaSolutions.lpptransit.data.lppapi.data.Station;
import com.VegaSolutions.lpptransit.utility.NetworkConnectivityManager;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TravanaApp extends Application {

    private NetworkConnectivityManager networkConnectivityManager;
    public NetworkConnectivityManager getNetworkConnectivityManager() {
        return networkConnectivityManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        networkConnectivityManager = new NetworkConnectivityManager(this);
    }

}
