package com.VegaSolutions.lpptransit;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.VegaSolutions.lpptransit.lppapi.responseobjects.Station;
import com.VegaSolutions.lpptransit.ui.custommaps.TravanaLocationManager;
import com.VegaSolutions.lpptransit.utility.NetworkConnectivityManager;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;

import java.util.ArrayList;

public class TravanaApp extends Application {

    public static final String TAG = "TravanaApp";
    private static TravanaApp instance;

    private NetworkConnectivityManager networkConnectivityManager;
    private TravanaLocationManager locationManager;
    private ArrayList<Station> stations = null;

    public static TravanaApp getInstance() {
        return TravanaApp.instance;
    }

    public NetworkConnectivityManager getNetworkConnectivityManager() {
        return networkConnectivityManager;
    }

    public TravanaLocationManager getLocationManager() {
        return locationManager;
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
        locationManager = new TravanaLocationManager(this);
    }

}
