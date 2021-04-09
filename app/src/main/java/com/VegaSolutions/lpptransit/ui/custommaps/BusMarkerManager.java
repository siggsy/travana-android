package com.VegaSolutions.lpptransit.ui.custommaps;

import com.VegaSolutions.lpptransit.lppapi.responseobjects.Bus;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.BusOnRoute;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusMarkerManager {

    private final Map<String, Marker> busMap;
    private final GoogleMap map;
    private final MarkerOptions options;
    private final MarkerAnimator animator;

    public BusMarkerManager(GoogleMap map, MarkerOptions options) {
        busMap = new HashMap<>();
        this.map = map;
        this.options = options;
        animator = new MarkerAnimator();
    }

    public void update(List<BusOnRoute> busesOnRoute) {

        // Remove all buses not on the list.
        for (Map.Entry<String, Marker> entry : busMap.entrySet()) {
            if (!contains(busesOnRoute, entry.getKey())) {
                entry.getValue().remove();
            }
        }

        // Add or update bus markers.
        for (BusOnRoute busOnRoute : busesOnRoute) {
            Marker bus = busMap.get(busOnRoute.getBusUnitId());
            if (bus != null)
                animator.animateMarker(bus, busOnRoute.getLatLng(), busOnRoute.getCardinalDirection(), new LatLngInterpolator.Linear());
            else {
                Marker marker = map.addMarker(options.position(busOnRoute.getLatLng()).rotation(busOnRoute.getCardinalDirection()));
                busMap.put(busOnRoute.getBusUnitId(), marker);
            }
        }

    }

    public void removeAllBuses() {
        // Remove all buses not on the list.
        for (Map.Entry<String, Marker> entry : busMap.entrySet()) {
            entry.getValue().remove();

        }
    }

    public void updateAll(List<Bus> buses) {

        // Remove all buses not on the list.
        for (Map.Entry<String, Marker> entry : busMap.entrySet()) {
            if (!containsAll(buses, entry.getKey()))
                entry.getValue().remove();
        }

        // Add or update bus markers.
        for (Bus bus : buses) {
            Marker busMarker = busMap.get(bus.getBusUnitId());
            if (busMarker != null)
                animator.animateMarker(busMarker, bus.getLatLng(), bus.getCardinalDirection(), new LatLngInterpolator.Linear());
            else {
                Marker marker = map.addMarker(options.position(bus.getLatLng()).rotation(bus.getCardinalDirection()));
                busMap.put(bus.getBusUnitId(), marker);
            }
        }
    }

    private boolean contains(List<BusOnRoute> busesOnRoute, String id) {
        for (BusOnRoute busOnRoute : busesOnRoute)
            if (busOnRoute.getBusUnitId().equals(id)) return true;
        return false;
    }

    private boolean containsAll(List<Bus> buses, String id) {
        for (Bus bus : buses)
            if (bus.getBusUnitId().equals(id)) return true;
        return false;
    }

}
