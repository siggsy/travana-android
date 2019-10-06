package com.VegaSolutions.lpptransit;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;

import com.VegaSolutions.lpptransit.animators.MapAnimator;
import com.VegaSolutions.lpptransit.lppapideprecated.Api;
import com.VegaSolutions.lpptransit.lppapideprecated.ApiCallback;
import com.VegaSolutions.lpptransit.lppapideprecated.responseclasses.Bus;
import com.VegaSolutions.lpptransit.lppapideprecated.responseclasses.Routes;
import com.VegaSolutions.lpptransit.lppapideprecated.responseclasses.StationsOnRoute;
import com.VegaSolutions.lpptransit.routing.RouteQuery;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LppApiManager {

    private static final String TAG = LppApiManager.class.getName();

    private GoogleMap map;
    private Activity activity;
    private Handler handler;

    private Routes currentRoute;

    private Map<Marker, Bus> buses = new HashMap<>();
    private Map<Marker, StationsOnRoute> stations = new HashMap<>();

    BusListener busListener;


    public LppApiManager(Activity activity, GoogleMap map) {
        this.activity = activity;
        this.map = map;
        map.setOnMarkerClickListener(marker -> {
            marker.showInfoWindow();
            return true;
        });

        handler = new Handler();
    }

    private Runnable busUpdater = new Runnable() {
        @Override
        public void run() {
            Api.busLocation(currentRoute.getInt_id(), (apiResponse, statusCode, success) -> {
                if (success) {
                    if (apiResponse.isSuccess()) {

                        // remove all
                        for (Map.Entry<Marker, Bus> entry : buses.entrySet())
                            activity.runOnUiThread(() -> entry.getKey().remove());
                        buses.clear();

                        for (Bus bus : apiResponse.getData())
                            busListener.onBusUpdate(bus);
                            //activity.runOnUiThread(() -> buses.put(map.addMarker(busStyle.position(bus.getGeometry().getLatLng())), bus));

                    }
                }
            });
            handler.postDelayed(this, 5000);
        }
    };

    // Query a new set of Stations and draw a route
    private ApiCallback<List<StationsOnRoute>> callback = ((apiResponse, statusCode, success) -> {
        if (success) {
            if (apiResponse.isSuccess()) {

                // sort stations by orderNo
                List<StationsOnRoute> stationsOnRoute = apiResponse.getData();
                Collections.sort(stationsOnRoute, (stationOnRoute, t1) -> Integer.compare(stationOnRoute.getOrder_no(), t1.getOrder_no()));

                // Create LatLng station array
                ArrayList<MapAnimator.StationIcon> stations = new ArrayList<>();
                ArrayList<LatLng> latLngs = new ArrayList<>();
                for (final StationsOnRoute station : stationsOnRoute) {
                    LatLng latLng = station.getGeometry().getLatLng();
                    latLngs.add(latLng);
                    activity.runOnUiThread(() -> stations.add(new MapAnimator.StationIcon(map.addMarker(MapAnimator.DEFAULT_OPTIONS.title(station.getName()).position(latLng)), BitmapFactory.decodeResource(activity.getResources(), R.drawable.filled_circle), .25f)));
                }

                // Query route and start animation
                new RouteQuery()
                        .addCoordinates(latLngs.toArray(new LatLng[0]))
                        .addListener((routeResponse, httpStatus, success1) -> {
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            for (LatLng latLng : latLngs)
                                builder.include(latLng);
                            LatLngBounds bounds = builder.build();
                            activity.runOnUiThread(() -> map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50)));
                            List<LatLng> route;
                            if (success1)
                                route = routeResponse.matchings.get(0).getGeometry().getLatLngList();
                            else
                                route = latLngs;
                            activity.runOnUiThread(() -> new MapAnimator(activity, map).animateRouteWithStations(stations, new PolylineOptions().addAll(latLngs).startCap(new RoundCap()).endCap(new RoundCap()).width(10), 500, 2000));
                        })
                        .execute();
            }
            else
                Log.e(TAG, "API server returned success boolean as FALSE!");
        } else
            Log.e(TAG, "Connection to API server FAILED!\nStatus Code:" + statusCode);
    });

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


    public interface BusListener {
        void onBusUpdate(Bus bus);
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
