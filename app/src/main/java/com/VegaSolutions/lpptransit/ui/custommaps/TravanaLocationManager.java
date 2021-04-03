package com.VegaSolutions.lpptransit.ui.custommaps;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.VegaSolutions.lpptransit.utility.MapUtility;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class TravanaLocationManager {

    private static final String TAG = TravanaLocationManager.class.getSimpleName();

    private final Context context;

    private LocationManager locationManager;
    private boolean gps, network = false;
    private static boolean live;

    private static LatLng latest;
    private static final List<TravanaLocationListener> listeners = new ArrayList<>();
    private final LocationListener mainListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            Log.i(TAG, "location updated: " + location.toString());

            if (location.getAccuracy() > 500)
                return;

            live = true;

            // Notify all listeners
            for (TravanaLocationListener listener : listeners)
                listener.onLocationChanged(location);

            // Save the latest
            latest = new LatLng(location.getLatitude(), location.getLongitude());

        }

        @Override
        public void onProviderEnabled(String provider) {

            Log.i(TAG, "Provider enabled: " + provider);

            // Notify listeners if at least one is enabled
            for (TravanaLocationListener listener : listeners)
                listener.onProviderAvailabilityChanged(true);

            if (provider.equals(LocationManager.GPS_PROVIDER))
                gps = true;
            else if (provider.equals(LocationManager.NETWORK_PROVIDER))
                network = true;

        }

        @Override
        public void onProviderDisabled(String provider) {

            Log.i(TAG, "Provider disabled: " + provider);

           if (provider.equals(LocationManager.GPS_PROVIDER))
               gps = false;
           else if (provider.equals(LocationManager.NETWORK_PROVIDER))
               network = false;

           // Notify listeners if both providers are disabled
           if (!gps  && !network)
               for (TravanaLocationListener listener : listeners)
                   listener.onProviderAvailabilityChanged(false);

        }

        // Redundant (Deprecated on API 29)
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    public TravanaLocationManager(Context context) {
        this.context = context;
    }

    public boolean addListener(TravanaLocationListener locationListener) {

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

    public void removeListener(TravanaLocationListener locationListener) {
        listeners.remove(locationListener);

        // Disable main listener if list is empty and save latest location
        if (listeners.isEmpty()) {
            enableMainProvider(false);
            saveLatest();
        }
    }

    public boolean isMainProviderEnabled() {
        return !listeners.isEmpty();
    }

    public boolean isLive() {
        return live;
    }

    private LatLng getLatestFromPreferences() {

        SharedPreferences preferences = context.getSharedPreferences("Latest_location", Context.MODE_PRIVATE);
        float lat = preferences.getFloat("lat", -91);
        float lng = preferences.getFloat("lng", -181);

        if (lat == -91 || lng == -181)
            return null;
        else return new LatLng(lat, lng);

    }

    private void saveLatest() {

        if (latest == null)
            return;

        SharedPreferences preferences = context.getSharedPreferences("Latest_location", Context.MODE_PRIVATE);
        preferences.edit().putFloat("lat", (float) latest.latitude).putFloat("lng", (float) latest.longitude).apply();

    }

    public LatLng getLatest() {
        if (latest == null)
            return getLatestFromPreferences();
        else return latest;
    }

    /**
     * Enables location requests.
     * @param value boolean if enable or not
     * @return boolean if successful
     */
    @SuppressLint("MissingPermission")
    private boolean enableMainProvider(boolean value) {

        if (value) {

            long minTime = 2000;
            float minDist = 50;

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
            } if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && (permission == 1 || permission == 3)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDist, mainListener);
                network = true;
            }
            return gps || network;

        } else {
            locationManager.removeUpdates(mainListener);
            return true;
        }

    }

    public interface TravanaLocationListener {
        void onLocationChanged(Location location);

        void onProviderAvailabilityChanged(boolean value);
    }

}
