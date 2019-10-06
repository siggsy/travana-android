package com.VegaSolutions.lpptransit.test;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Toast;

import com.VegaSolutions.lpptransit.LppApiManager;
import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.animators.MapAnimator;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.ApiCallback;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.ApiResponse;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.StationOnRoute;
import com.VegaSolutions.lpptransit.ui.bottomfragments.MainFragment;
import com.VegaSolutions.lpptransit.lppapideprecated.responseclasses.Routes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class MapsTestActivity extends FragmentActivity implements
        OnMapReadyCallback,
        MainFragment.OnFragmentInteractionListener,
        MapAnimator.MapAnimatorListener{


    private LatLng currentLocation;
    private GoogleMap mMap;

    boolean couter = false;

    ImageButton back;

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

            //Log.i("locationUpdate", currentLocation.toString());
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    /*SearchView search;
    ListView routeList;
    Button button;*/

    ArrayList<String> routeName = new ArrayList<>();
    Map<String, Routes> routes = new HashMap<>();
    ArrayAdapter<String> arrayAdapter;

    LppApiManager mapManager;
    MainFragment.OnFragmentInteractionListener listener;

    @SuppressLint("MissingPermission")
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

        back = findViewById(R.id.maps_test_back_btn);
        back.setOnClickListener(view -> {
            switchFragment(MainFragment.newInstance());
            new MapAnimator(this, mMap).removeMarkers(500);
        });

    }

    private void switchFragment(Fragment fragment) {

        FragmentManager manager = this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.replace(R.id.bottom_sheet_fragment, fragment);
        fragmentTransaction.commit();

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
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);

            switchFragment(MainFragment.newInstance());
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            }
        }

        List<LatLng> latLngs = new ArrayList<>();
        Api.stationsOnRoute(getIntent().getStringExtra("trip_id"), (apiResponse, statusCode, success) -> {
            for (StationOnRoute stationOnRoute : apiResponse.getData()) {
                latLngs.add(new LatLng(stationOnRoute.getLatitude(), stationOnRoute.getLongitude()));
            }
            runOnUiThread(() -> mMap.addPolyline(new PolylineOptions().addAll(latLngs).width(5f)));
        });
        //mMap.setOnMyLocationChangeListener(location -> currentLocation = new LatLng(location.getLatitude(), location.getLongitude()));
        mapManager = new LppApiManager(this, mMap);



        runOnUiThread(() -> mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(46.056319, 14.505381), 12)));

    }

    @Override
    public void onFragmentInteraction(int uri) {
        if (uri == MainFragment.NEARBY_BUTTON) {
            if (currentLocation != null) {
            }
            else
                Toast.makeText(this, "Location error!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRemoveAnimationFinished(Marker marker) {
        runOnUiThread(marker::remove);
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

    public static double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;
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
