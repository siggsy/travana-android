package com.VegaSolutions.lpptransit.ui.custommaps;

import android.content.Context;
import android.location.Location;

import androidx.core.content.ContextCompat;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.utility.MapUtility;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationMarkerManager {

    private GoogleMap map;

    // Marker objects
    private Marker marker;
    private Circle accuracyCircle;
    private MarkerOptions options;
    private CircleOptions accuracyOptions;
    private BitmapDescriptor live, offline;
    private MarkerAnimator animator;

    public LocationMarkerManager(Context context, GoogleMap map, LatLng latest, BitmapDescriptor live, BitmapDescriptor offline) {
        this.map = map;

        // Set marker style
        options = new MarkerOptions().anchor(0.5f, 0.5f).icon(offline).flat(true).zIndex(10f);
        accuracyOptions = new CircleOptions().fillColor(ContextCompat.getColor(context, R.color.colorRipple_map)).strokeColor(ContextCompat.getColor(context, R.color.main_blue)).strokeWidth(2f);

        this.live = live;
        this.offline = offline;
        animator = new MarkerAnimator();

        // Set "Offline" marker if latest is not null
        if (latest != null)
            marker = map.addMarker(options.position(latest));

    }

    public void update(Location location) {

        LatLng latLng = MapUtility.getLatLngFromLocation(location);

        if (marker == null)
            marker = map.addMarker(options.position(latLng).icon(live));
        if (accuracyCircle == null)
            accuracyCircle = map.addCircle(accuracyOptions.center(latLng).radius(location.getAccuracy()));

        animator.animateMarkerWithCircle(marker, accuracyCircle, location.getAccuracy(), latLng, new LatLngInterpolator.Linear());

        setLive(true);

    }

    public void setLive(boolean value) {
        if (marker != null)
            marker.setIcon(value ? live : offline);
        if (accuracyCircle != null && !value)
            accuracyCircle.setRadius(0);
    }

}
