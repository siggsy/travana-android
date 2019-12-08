package com.VegaSolutions.lpptransit.ui.activities;

import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.ui.custommaps.LocationMarkerManager;
import com.VegaSolutions.lpptransit.ui.custommaps.MyLocationManager;
import com.VegaSolutions.lpptransit.utility.MapUtility;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

public abstract class MapFragmentActivity extends FragmentActivity implements OnMapReadyCallback, MyLocationManager.MyLocationListener {

    protected GoogleMap mMap;

    protected LatLng ljubljana = new LatLng(46.056319, 14.505381);
    protected MyLocationManager locationManager;
    protected LocationMarkerManager markerManager;

    protected ImageView locationIcon;


    @Override
    protected void onPause() {
        super.onPause();
        if (locationManager != null)
            locationManager.removeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (locationManager != null)
            locationManager.addListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // Setup generic UI
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setMapStyle(new MapStyleOptions(ViewGroupUtils.isDarkTheme(this) ? getString(R.string.dark_2) : getString(R.string.white)));

        // Set location button location callback.
        if (MapUtility.checkLocationPermission(this)) {

            // Setup location objects
            locationManager = new MyLocationManager(this);
            markerManager = new LocationMarkerManager(this, mMap,
                    locationManager.getLatest(),
                    MapUtility.getMarkerIconFromDrawable(ContextCompat.getDrawable(this, R.drawable.current_location_live)),
                    MapUtility.getMarkerIconFromDrawable(ContextCompat.getDrawable(this, R.drawable.current_location_offline)));

            markerManager.setLive(locationManager.isLive());
            locationManager.addListener(this);

            locationIcon.setVisibility(View.VISIBLE);
            locationIcon.setOnClickListener(v -> {
                LatLng location = locationManager.getLatest();
                if (location != null)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
            });

        }

        // Set camera to Ljubljana.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ljubljana, 11.5f));

    }

    @Override
    public void onLocationChanged(Location location) {
        if (markerManager != null)
            markerManager.update(location);
    }

    @Override
    public void onProviderAvailabilityChanged(boolean value) {
        if (markerManager != null)
            markerManager.setLive(value);
    }

}
