package com.VegaSolutions.lpptransit.ui.custommaps;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MyLocationManager {

    private static final String TAG = MyLocationManager.class.getSimpleName();

    private Context context;

    private LocationManager locationManager;
    private String provider;

    private static LatLng latest;
    private static List<MyLocationListener> listeners = new ArrayList<>();
    private LocationListener mainListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            Log.i(TAG, "location updated: " + location.toString());

            // Notify all listeners
            for (MyLocationListener listener : listeners)
                listener.onLocationChanged(location);

            // Save the latest
            latest = new LatLng(location.getLatitude(), location.getLongitude());

        }

        @Override
        public void onProviderEnabled(String provider) {

            Log.i(TAG, "Provider enabled: " + provider);

            if (provider.equals(MyLocationManager.this.provider))
                for (MyLocationListener listener : listeners)
                    listener.onProviderAvailabilityChanged(true);
        }

        @Override
        public void onProviderDisabled(String provider) {

            Log.i(TAG, "Provider disabled: " + provider);

            if (provider.equals(MyLocationManager.this.provider))
                for (MyLocationListener listener : listeners)
                    listener.onProviderAvailabilityChanged(false);
        }

        // Redundant (Deprecated on API 29)
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    public MyLocationManager(Context context) {
        this.context = context;
    }

    public boolean addListener(MyLocationListener locationListener) {

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

    public void removeListener(MyLocationListener locationListener) {
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

            // Set criteria for the best provider
            Criteria criteria = new Criteria();
            criteria.setBearingRequired(false);
            criteria.setAltitudeRequired(false);
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);

            // Get service if null
            if (locationManager == null)
                locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            provider = locationManager.getBestProvider(criteria, true);

            // Return result false if getting best provider failed
            if (provider == null)
                return false;

            // Request location updates and return true for success
            locationManager.requestLocationUpdates(provider, minTime, minDist, mainListener);
            return true;

        } else {
            locationManager.removeUpdates(mainListener);
            return true;
        }

    }

    public interface MyLocationListener {
        void onLocationChanged(Location location);
        void onProviderAvailabilityChanged(boolean value);
    }

}
