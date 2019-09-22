package com.VegaSolutions.lpptransit.test;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.VegaSolutions.lpptransit.MapManager;
import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.ApiCallback;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.ApiResponse;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Bus;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Routes;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.StationsOnRoute;
import com.VegaSolutions.lpptransit.routing.Route;
import com.VegaSolutions.lpptransit.routing.RouteQuery;
import com.VegaSolutions.lpptransit.routing.RouteResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsTestActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    SearchView search;
    ListView routeList;
    int routeId = 1579;

    ArrayList<String> routeName = new ArrayList<>();
    Map<String, Routes> routes = new HashMap<>();
    ArrayAdapter<String> arrayAdapter;

    MapManager mapManager;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        search = findViewById(R.id.routeSearch);
        routeList = findViewById(R.id.routeList);
        routeList.bringToFront();
        routeList.setOnItemClickListener((adapterView, view, i, l) -> {
            String item = arrayAdapter.getItem(i);
            mapManager.setRoute(routes.get(item));
            runOnUiThread(() -> {
                routeList.setVisibility(View.GONE);
                search.clearFocus();
            });
        });

        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, routeName);
        routeList.setAdapter(arrayAdapter);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                arrayAdapter.getFilter().filter(s);
                return true;
            }
        });
        search.setOnSearchClickListener(view -> Api.getRoutes((apiResponse, statusCode, success) -> {
            if (success) {
                runOnUiThread(() -> {
                    routeList.bringToFront();
                    routeList.setVisibility(View.VISIBLE);
                });
                runOnUiThread(() -> arrayAdapter.clear());
                routes = new HashMap<>();
                for (Routes route : apiResponse.getData()) {
                    String name = route.getGroup_name() + " " + route.getParent_name();
                    runOnUiThread(() -> arrayAdapter.add(name));
                    routes.put(name, route);
                }
            }
        }));
        search.setOnCloseListener(() -> {
            runOnUiThread(() -> routeList.setVisibility(View.GONE));
            return false;
        });
        search.setOnQueryTextFocusChangeListener((view, b) -> {
            if (!b) {
                runOnUiThread(() -> routeList.setVisibility(View.GONE));
            } else {
                runOnUiThread(() -> {
                    routeList.bringToFront();
                    routeList.setVisibility(View.VISIBLE);
                });
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mapManager = new MapManager(this, mMap);
        mapManager.setStationStyle(new MarkerOptions().anchor(0.5f, 0.5f).icon(bitmapDescriptorFromDrawable(R.drawable.circle, 20)));
        mapManager.setBusStyle(new MarkerOptions().anchor(0.5f, 0.5f).icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_navigation_black_24dp)).zIndex(1f));


        runOnUiThread(() -> {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(46.056319, 14.505381), 12));
        });

    }

    /**
     * Start route animation.
     */
    /*private void startAnim(){
        if(mMap != null) {
            MapAnimator.getInstance(mMap).animateRoute(mMap, route);
        } else {
            Toast.makeText(getApplicationContext(), "Map not ready", Toast.LENGTH_LONG).show();
        }
    }*/

    /**
     * Reset route animation.
     */
    /*public void resetAnimation(){
        startAnim();
    }*/


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

    /**
     * Class with sole purpose for testing
     */
    private class Average {

        private Map<String, ArrayList<String>> bus_map;

        public Average() {
            bus_map = new HashMap<>();
        }

        public void updateAverage(String bus, String date) {
            if (bus_map.containsKey(bus)) {
                int dateI = -1;
                ArrayList<String> get = bus_map.get(bus);
                for (int i = 0, getSize = get.size(); i < getSize; i++) {
                    String a = get.get(i);
                    if (date.equals(a)) dateI = i;
                }
                if (dateI == -1) {
                    bus_map.get(bus).add(date);
                }
            } else {
                ArrayList<String> dates = new ArrayList<>();
                dates.add(date);
                bus_map.put(bus, dates);
            }
        }


        @Override
        public String toString() {

            ArrayList<String> list = new ArrayList<>();

            for (Map.Entry<String, ArrayList<String>> entry : bus_map.entrySet()) {
                StringBuilder a = new StringBuilder(entry.getKey() + ": ");
                for (String b : entry.getValue()) {
                    a.append(b).append(", ");
                }
                list.add(a.toString());
            }
            StringBuilder a = new StringBuilder();
            for (String b : list) {
                a.append(b).append('\n');
            }

            return a.toString();
        }

    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private BitmapDescriptor bitmapDescriptorFromDrawable(@DrawableRes int drawable, int px) {

        Bitmap mDotMarkerBitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mDotMarkerBitmap);
        Drawable shape = getResources().getDrawable(drawable);
        shape.setBounds(0, 0, mDotMarkerBitmap.getWidth(), mDotMarkerBitmap.getHeight());
        shape.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(mDotMarkerBitmap);

    }

}
