package com.VegaSolutions.lpptransit;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.VegaSolutions.lpptransit.lppapi.data.Station;
import com.VegaSolutions.lpptransit.utility.NetworkConnectivityManager;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TravanaApp extends Application {

    public static final String TAG = "TravanaApp";
    private static TravanaApp instance;

    private Api api;
    private NetworkConnectivityManager networkConnectivityManager;
    private ArrayList<Station> stations = null;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(2);

    public static TravanaApp getInstance() {
        return TravanaApp.instance;
    }

    public NetworkConnectivityManager getNetworkConnectivityManager() {
        return networkConnectivityManager;
    }

    public Api getApi() {
        return api;
    }

    public ExecutorService getThreadPool() {
        return threadPool;
    }

    public boolean areStationsLoaded() {
        return stations != null;
    }

    public synchronized ArrayList<Station> getStations() {
        return stations;
    }

    public synchronized void setStations(ArrayList<Station> stations) {
        this.stations = stations;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(ViewGroupUtils
                .Theme.valueOf(
                        getSharedPreferences("settings", MODE_PRIVATE)
                                .getString("application_theme", "AUTO")
                ).value);

        // App singletons
        instance = this;
        networkConnectivityManager = new NetworkConnectivityManager(this);
        api = new Api(this);
    }

}
