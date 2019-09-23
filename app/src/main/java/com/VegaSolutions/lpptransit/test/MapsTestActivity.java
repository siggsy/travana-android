package com.VegaSolutions.lpptransit.test;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.VegaSolutions.lpptransit.MapManager;
import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.animators.MapAnimator;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.ApiCallback;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.ApiResponse;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Bus;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.LiveBusArrivalV2;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Routes;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.StationsInRange;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsTestActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    /*SearchView search;
    ListView routeList;
    Button button;*/

    LinearLayout bottom_sheet;

    ArrayList<String> routeName = new ArrayList<>();
    Map<String, Routes> routes = new HashMap<>();
    ArrayAdapter<String> arrayAdapter;

    MapManager mapManager;

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




        bottom_sheet = findViewById(R.id.bottom_sheet_ll);
        //search = findViewById(R.id.routeSearch);
        //routeList = findViewById(R.id.routeList);
        //button = findViewById(R.id.button);

        /*routeList.bringToFront();
        routeList.setOnItemClickListener((adapterView, view, i, l) -> {
            String item = arrayAdapter.getItem(i);
            mapManager.setRoute(routes.get(item));
            runOnUiThread(() -> {
                routeList.setVisibility(View.GONE);
                search.clearFocus();
            });
        });

        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, routeName);
        routeList.setAdapter(arrayAdapter);*/
        /*search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        });*/


    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 100) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            } else {
                // Permission was denied. Display an error message.
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }

        mMap.setOnMyLocationClickListener(location -> {
            LatLng position = new LatLng(location.getLatitude(), location.getLongitude());//new LatLng(46.051753, 14.503238);
            Api.stationsInRange(500, position.latitude, position.longitude, (apiResponse, statusCode, success) -> {
                if (success) {
                    runOnUiThread(() -> {
                        mMap.clear();
                        bottom_sheet.removeAllViews();
                    });
                    List<StationsInRange> stationsInRange = apiResponse.getData();
                    List<MapAnimator.StationIcon> stationIcons = new ArrayList<>();
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (StationsInRange station : stationsInRange) {
                        LatLng latLng = station.getGeometry().getLatLng();
                        builder.include(latLng);
                        if (station.getName() != null && !station.getName().equals("")) {
                            stationIcons.add(new MapAnimator.StationIcon(new MarkerOptions().position(latLng).title(station.getName()), BitmapFactory.decodeResource(getResources(), R.drawable.filled_circle), .25f));
                            runOnUiThread(() -> {
                                View v = getLayoutInflater().inflate(R.layout.station_in_range, bottom_sheet, false);
                                TextView name = v.findViewById(R.id.station_in_range_name_tv);
                                TextView distance = v.findViewById(R.id.station_in_range_range_tv);
                                distance.setText(Math.round(CalculationByDistance(position, latLng)* 100)  + " m");
                                name.setText(station.getName());
                                final LinearLayout ll = v.findViewById(R.id.station_in_range_ll);
                                Api.liveBusArrivalV2(station.getInt_id(), (apiResponse1, statusCode1, success1) -> {
                                    runOnUiThread(() -> {

                                        LiveBusArrivalV2 arrivalV2 = apiResponse1.getData();

                                        for(LiveBusArrivalV2.Route route : arrivalV2.getRoutes()) {
                                            String routeNumber = route.getRoute_group_number();

                                            for (LiveBusArrivalV2.Arrival arrival : route.getArrivals()) {
                                                View view = getLayoutInflater().inflate(R.layout.adapter_live_arrivals, ll, false);
                                                TextView number = view.findViewById(R.id.live_arrival_number);
                                                TextView nameGroup = view.findViewById(R.id.live_arrival_name);
                                                TextView minutes = view.findViewById(R.id.live_arrival_minutes);

                                                number.setText(routeNumber);
                                                nameGroup.setText(arrival.getRoute_specific_name());
                                                minutes.setText(arrival.getEta() + " min");

                                                ll.addView(view);

                                            }
                                        }


                                    });
                                });


                                bottom_sheet.addView(v);
                            });
                        }
                    }
                    if (stationsInRange.size() > 0) {
                        LatLngBounds bounds = builder.build();
                        runOnUiThread(() -> {
                            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
                            new MapAnimator(mMap).animateStations(stationIcons, 500);

                        });
                    }


                }
            });
        });

        mapManager = new MapManager(this, mMap);
        mapManager.setStationStyle(new MarkerOptions().anchor(0.5f, 0.5f).icon(bitmapDescriptorFromDrawable(R.drawable.circle, 20)));
        mapManager.setBusStyle(new MarkerOptions().anchor(0.5f, 0.5f).icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_navigation_black_24dp)).zIndex(1f));



        runOnUiThread(() -> mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(46.056319, 14.505381), 12)));

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

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }

}
