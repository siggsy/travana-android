package com.VegaSolutions.lpptransit;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.Nullable;

import com.VegaSolutions.lpptransit.animators.RouteAnimation;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.ApiCallback;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.ApiResponse;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Bus;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Geometry;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Routes;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.StationsOnRoute;
import com.VegaSolutions.lpptransit.routing.MathingsResponse;
import com.VegaSolutions.lpptransit.routing.RouteQuery;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.ButtCap;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.LogRecord;

public class MapManager {

    private static final String TAG = MapManager.class.getName();

    private GoogleMap map;
    private Activity activity;
    private Handler handler;

    private Routes currentRoute;

    private Map<Marker, Bus> buses = new HashMap<>();
    private Map<Marker, StationsOnRoute> stations = new HashMap<>();

    private MarkerOptions stationStyle = new MarkerOptions();
    private MarkerOptions busStyle = new MarkerOptions();

    /*private Runnable busUpdater = new Runnable() {
        @Override
        public void run() {
            Api.busLocation(currentRoute.getInt_id(), (apiResponse, statusCode, success) -> {
                if (success) {
                    if (apiResponse.isSuccess()) {
                        // remove all
                        for (Map.Entry<Marker, Bus> entry : buses.entrySet())
                            activity.runOnUiThread(() -> entry.getKey().remove());
                        buses.clear();
                        for (Bus bus : apiResponse.getData()) {
                            double lat = bus.getGeometry().getCoordinates()[1];
                            double lon = bus.getGeometry().getCoordinates()[0];
                            activity.runOnUiThread(() -> buses.put(map.addMarker(busStyle.position(new LatLng(lat, lon))), bus));
                            Log.i(bus.getReg_number(), bus.getSpeed() + "");
                        }
                    }
                }
            });
            handler.postDelayed(this, 5000);
        }
    };*/


    // Query a new set of Stations and draw a route
    private ApiCallback<List<StationsOnRoute>> callback = ((apiResponse, statusCode, success) -> {
        if (success) {
            if (apiResponse.isSuccess()) {
                List<StationsOnRoute> stationsOnRoute = apiResponse.getData();
                Collections.sort(stationsOnRoute, (stationOnRoute, t1) -> Integer.compare(stationOnRoute.getOrder_no(), t1.getOrder_no()));
                ArrayList<LatLng> latLngs = new ArrayList<>();
                for (final StationsOnRoute station : stationsOnRoute) {
                    final double[] coordinates = station.getGeometry().getCoordinates();
                    //activity.runOnUiThread(() -> stations.put(map.addMarker(stationStyle.position(new LatLng(coordinates[1], coordinates[0])).title(station.getName())), station));
                    LatLng latLng = new LatLng(coordinates[1], coordinates[0]);
                    //activity.runOnUiThread(() -> map.addMarker(stationStyle.title(station.getName()).position(latLng)));
                    latLngs.add(latLng);
                }
                new RouteQuery()
                        .addCoordinates(latLngs.toArray(new LatLng[0]))
                        .addListener((routeResponse, httpStatus, success1) -> {
                            if (success1) {
                                List<LatLng> route = new ArrayList<>();
                                for (double[] coordinates : routeResponse.matchings.get(0).getGeometry().getCoordinates()) {
                                    route.add(new LatLng(coordinates[1], coordinates[0]));
                                }

                                //activity.runOnUiThread(() -> map.addMarker(stationStyle.position(latLngs.get(latLngs.size() - 1))));
                                activity.runOnUiThread(() -> map.addPolyline(new PolylineOptions().addAll(route).width(10).startCap(new RoundCap()).endCap(new RoundCap())));
                            }
                        })
                        .execute();

                //activity.runOnUiThread(() -> map.addPolyline(new PolylineOptions().addAll(asList(latLngs)).width(5f)));
            }
            else
                Log.e(TAG, "API server returned success boolean as FALSE!");
        } else
            Log.e(TAG, "Connection to API server FAILED!\nStatus Code:" + statusCode);
    });

    public MapManager(Activity activity, GoogleMap map) {
        this.activity = activity;
        this.map = map;
        map.setOnMarkerClickListener(marker -> {
            marker.showInfoWindow();
            return true;
        });

        handler = new Handler();
    }

    public void setStationStyle(MarkerOptions markerOptions) {
        this.stationStyle = markerOptions;
    }
    public void setBusStyle(MarkerOptions markerOptions) {
        this.busStyle = markerOptions;
    }

    public Bus getBus(Marker marker) {
        return buses.get(marker);
    }

    public StationsOnRoute getStation(Marker marker) {
        return  stations.get(marker);
    }

    public void setRoute(Routes route) {

        //handler.removeCallbacks(busUpdater);
        currentRoute = route;
        map.clear();
        // Remove all previous markers;
        for (Map.Entry<Marker, StationsOnRoute> entry : stations.entrySet())
            entry.getKey().remove();
        stations.clear();
        Log.i("route_id", route.getInt_id() + "");
        Log.i("route_name", route.getParent_name());

        Api.getStationsOnRoute(route.getInt_id(), callback);
        //Api.getStationsOnRoute(route.getOpposite_route_int_id(), callback);

        //handler.post(busUpdater);

    }

    /**
     * Convert SparseArray to List
     * @param sparseArray array to be converted
     * @param <C> type of the array
     * @return List representing sparse array
     */
    public <C> List<C> asList(SparseArray<C> sparseArray) {
        if (sparseArray == null) return null;
        List<C> arrayList = new ArrayList<C>(sparseArray.size());
        for (int i = 0; i < sparseArray.size(); i++)
            arrayList.add(sparseArray.valueAt(i));
        return arrayList;
    }

}
