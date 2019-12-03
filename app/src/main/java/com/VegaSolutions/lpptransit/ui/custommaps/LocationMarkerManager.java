package com.VegaSolutions.lpptransit.ui.custommaps;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationMarkerManager {

    private GoogleMap map;

    // Marker objects
    private Marker marker;
    private MarkerOptions options;
    private BitmapDescriptor live, offline;
    private MarkerAnimator animator;

    public LocationMarkerManager(GoogleMap map, LatLng latest, BitmapDescriptor live, BitmapDescriptor offline) {
        this.map = map;

        // Set marker style
        options = new MarkerOptions().anchor(0.5f, 0.5f).icon(offline).flat(true);
        this.live = live;
        this.offline = offline;
        animator = new MarkerAnimator();

        // Set "Offline" marker if latest is not null
        if (latest != null)
            marker = map.addMarker(options.position(latest));

    }

    public void update(LatLng location) {

        if (marker == null)
            marker = map.addMarker(options.position(location).icon(live));
        else animator.animateMarker(marker, location, 0, new LatLngInterpolator.Linear());
        setLive(true);

    }

    private void setLive(boolean value) {
        marker.setIcon(value ? live : offline);
    }

}
