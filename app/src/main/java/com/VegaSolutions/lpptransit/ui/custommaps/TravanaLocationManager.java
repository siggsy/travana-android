package com.VegaSolutions.lpptransit.ui.custommaps;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.VegaSolutions.lpptransit.utility.MapUtility;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashSet;
import java.util.Set;

public class TravanaLocationManager {

    private static final String TAG = TravanaLocationManager.class.getSimpleName();

    private final Context context;

    private LocationManager locationManager;
    private boolean gps, network = false;
    private boolean live = false;

    private LatLng latest = null;

    private final Set<TravanaLocationListener> listeners = new HashSet<>();
    private final LocationListener mainListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            synchronized (this) {
                Log.i(TAG, "location updated: " + location.toString());

                if (location.getAccuracy() > 250) {
                    return;
                }

                // Notify first live location
                if (!live) {
                    for (TravanaLocationListener listener : listeners) {
                        listener.onProviderAvailabilityChanged(true);
                    }
                }
                live = true;

                // Notify all listeners
                for (TravanaLocationListener listener : listeners)
                    listener.onLocationChanged(location);

                // Save the latest
                latest = new LatLng(location.getLatitude(), location.getLongitude());
            }
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
            synchronized (this) {
                Log.i(TAG, "Provider enabled: " + provider);
                if (provider.equals(LocationManager.GPS_PROVIDER))
                    gps = true;
                else if (provider.equals(LocationManager.NETWORK_PROVIDER))
                    network = true;
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            synchronized (this) {
                Log.i(TAG, "Provider disabled: " + provider);

                if (provider.equals(LocationManager.GPS_PROVIDER))
                    gps = false;
                else if (provider.equals(LocationManager.NETWORK_PROVIDER))
                    network = false;
            }
        }

        // Redundant (Deprecated on API 29)
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    public TravanaLocationManager(Context context) {
        this.context = context;
    }

    public synchronized boolean addListener(TravanaLocationListener locationListener) {

        // Enable main listener if this is the first one
        if (listeners.size() == 0) {
            if (enableMainProvider(true)) {
                listeners.add(locationListener);
                return true;
            } else return false;
        }
        listeners.add(locationListener);
        return true;
    }

    public synchronized void removeListener(TravanaLocationListener locationListener) {
        listeners.remove(locationListener);

        // Disable main listener if list is empty and save latest location
        if (listeners.isEmpty()) {
            enableMainProvider(false);
            saveLatest();
        }
    }

    public synchronized boolean isLive() {
        return live;
    }

    public synchronized LatLng getLatest() {
        if (latest == null)
            return getLatestFromPreferences();
        else return latest;
    }

    private LatLng getLatestFromPreferences() {
        SharedPreferences preferences = context.getSharedPreferences("Latest_location", Context.MODE_PRIVATE);
        float lat = preferences.getFloat("lat", Float.NaN);
        float lng = preferences.getFloat("lng", Float.NaN);

        if (Float.isNaN(lat) || Float.isNaN(lng))
            return null;
        else return new LatLng(lat, lng);
    }

    private void saveLatest() {
        if (latest == null)
            return;

        SharedPreferences preferences = context.getSharedPreferences("Latest_location", Context.MODE_PRIVATE);
        preferences.edit().putFloat("lat", (float) latest.latitude).putFloat("lng", (float) latest.longitude).apply();
    }

    /**
     * Enables location requests.
     * @param value boolean if enable or not
     * @return boolean if successful
     */
    @SuppressLint("MissingPermission")
    private boolean enableMainProvider(boolean value) {

        if (value) {
            // Avoid repeated requests for location updates
            if (network || gps)
                return true;

            long minTime = 2000;
            float minDist = 20;

            // Get service if null
            if (locationManager == null)
                locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager == null)
                return false;

            int permission = MapUtility.getGrantedLocationPermission(context);

            // Request location updates and return true for success
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && (permission == 2 || permission == 3)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDist, mainListener);
                gps = true;
            // Enable network provider as fallback but not alongside
            } if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && (permission == 1 || permission == 3)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDist, mainListener);
                network = true;
            }
            return gps || network;

        } else {
            network = false;
            gps = false;
            locationManager.removeUpdates(mainListener);
            return true;
        }

    }

    public interface TravanaLocationListener {
        void onLocationChanged(Location location);

        void onProviderAvailabilityChanged(boolean value);
    }

}
