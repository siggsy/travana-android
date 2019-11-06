package com.VegaSolutions.lpptransit.ui.custommaps;

import android.app.Activity;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.VegaSolutions.lpptransit.lppapi.responseobjects.Bus;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.BusOnRoute;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusMarkerManager {

    private Map<String, Marker> busMap;
    private GoogleMap map;
    private MarkerOptions options;

    public BusMarkerManager(GoogleMap map, MarkerOptions options) {
        busMap = new HashMap<>();
        this.map = map;
        this.options = options;
    }

    public void update(List<BusOnRoute> busesOnRoute) {

        for (Map.Entry<String, Marker> entry : busMap.entrySet()) {
            if (!contains(busesOnRoute, entry.getKey()))
                entry.getValue().remove();
        }

        for (BusOnRoute busOnRoute : busesOnRoute) {
            Marker bus = busMap.get(busOnRoute.getBus_unit_id());
            if (bus != null)
               animateMarker(bus, busOnRoute.getLatLng(), busOnRoute.getCardinal_direction(), new LatLngInterpolator.Linear());
            else {
                Marker marker = map.addMarker(options.position(busOnRoute.getLatLng()).rotation(busOnRoute.getCardinal_direction()));
                busMap.put(busOnRoute.getBus_unit_id(), marker);
            }
        }
    }

    public void updateAll(List<Bus> buses) {
        for (Map.Entry<String, Marker> entry : busMap.entrySet()) {
            if (!containsAll(buses, entry.getKey()))
                entry.getValue().remove();
        }

        for (Bus bus : buses) {
            Marker busMarker = busMap.get(bus.getBus_unit_id());
            if (busMarker != null)
                animateMarker(busMarker, bus.getLatLng(), bus.getCardinal_direction(), new LatLngInterpolator.Linear());
            else {
                Marker marker = map.addMarker(options.position(bus.getLatLng()).rotation(bus.getCardinal_direction()));
                busMap.put(bus.getBus_unit_id(), marker);
            }
        }
    }

    private boolean contains(List<BusOnRoute> busesOnRoute, String id) {
        for (BusOnRoute busOnRoute : busesOnRoute)
            if (busOnRoute.getBus_unit_id().equals(id)) return true;
        return false;
    }

    private boolean containsAll(List<Bus> buses, String id) {
        for (Bus bus : buses)
            if (bus.getBus_unit_id().equals(id)) return true;
        return false;
    }

    private void animateMarker(Marker marker, LatLng finalPosition, float finalCardinalDirection,  LatLngInterpolator latLngInterpolator) {

        final LatLng startPosition = marker.getPosition();
        float startCardinalDirection = marker.getRotation();
        final float beginCardinalDirection;
        final float endCardinalDirection;

        // Adjust angle animation (shortest path)
        if (finalCardinalDirection - startCardinalDirection > 180) {
            if (startCardinalDirection < 0)
                startCardinalDirection = startCardinalDirection + 360;
            else finalCardinalDirection = finalCardinalDirection - 360;
        }

        endCardinalDirection = finalCardinalDirection;
        beginCardinalDirection = startCardinalDirection;
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 1000;

        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);

                marker.setPosition(latLngInterpolator.interpolate(v, startPosition, finalPosition));
                marker.setRotation(v * (endCardinalDirection - beginCardinalDirection) + beginCardinalDirection);

                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

}
