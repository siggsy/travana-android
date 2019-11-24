package com.VegaSolutions.lpptransit.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.ApiCallback;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Station;
import com.VegaSolutions.lpptransit.ui.custommaps.BusMarkerManager;
import com.VegaSolutions.lpptransit.ui.custommaps.CustomClusterRenderer;
import com.VegaSolutions.lpptransit.ui.custommaps.StationInfoWindow;
import com.VegaSolutions.lpptransit.ui.custommaps.StationMarker;
import com.VegaSolutions.lpptransit.ui.errorhandlers.TopMessage;
import com.VegaSolutions.lpptransit.ui.fragments.HomeFragment;
import com.VegaSolutions.lpptransit.ui.fragments.StationsFragment;
import com.VegaSolutions.lpptransit.utility.MapUtility;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.maps.android.clustering.ClusterManager;

import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

import biz.laenger.android.vpbs.ViewPagerBottomSheetBehavior;

import static com.VegaSolutions.lpptransit.ui.fragments.HomeFragment.BIKE;
import static com.VegaSolutions.lpptransit.ui.fragments.HomeFragment.BUS;
import static com.VegaSolutions.lpptransit.ui.fragments.HomeFragment.PARKING;
import static com.VegaSolutions.lpptransit.ui.fragments.HomeFragment.TRAIN;


// TODO: Clean the code, fix MapPadding remove useless callbacks and variables

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, StationsFragment.StationsFragmentListener, HomeFragment.HomeFragmentListener, NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;

    LatLng ljubljana = new LatLng(46.056319, 14.505381);
    FusedLocationProviderClient fusedLocationProviderClient;
    private final int locationRequestCode = 1000;

    ViewPagerBottomSheetBehavior behavior;

    Stack<Fragment> fragments = new Stack<>();

    ImageButton navbar_button;
    ImageButton search;
    View shadow;
    TopMessage loading;
    ImageView location_icon;

    DrawerLayout dl;
    NavigationView nv;

    View bottom_sheet;
    BusMarkerManager markerManager;
    private ClusterManager<StationMarker> clusterManager;

    private ApiCallback<List<Station>> callback = (apiResponse, statusCode, success) -> {
        if (success) {
            onStationsUpdated(apiResponse.getData(), true, statusCode);
        } else {
            onStationsUpdated(null, false, statusCode);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == SettingsActivity.SETTINGS_UPDATE) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ViewGroupUtils.isDarkTheme(this) ? R.style.DarkTheme : R.style.WhiteTheme);
        setContentView(R.layout.app_nav_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    locationRequestCode);

        }

        dl = findViewById(R.id.nav_layout);
        nv = findViewById(R.id.nv);

        nv.setNavigationItemSelectedListener(this);

        search = findViewById(R.id.search);
        navbar_button = findViewById(R.id.account);
        location_icon = findViewById(R.id.maps_location_icon);
        search.setOnClickListener(view -> startActivity(new Intent(this, SearchActivity.class)));
        navbar_button.setOnClickListener(view -> dl.openDrawer(GravityCompat.START));
        if (MapUtility.checkLocationPermission(this)) {
            location_icon.setVisibility(View.VISIBLE);
        } else {
            location_icon.setVisibility(View.GONE);
        }
        shadow = findViewById(R.id.shadow);
        loading = findViewById(R.id.top_message);
        loading.showLoading(true);
        loading.setErrorMsgBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
        loading.setErrorMsgColor(Color.WHITE);
        loading.setErrorIconColor(Color.WHITE);
        loading.setRefreshClickEvent(v -> {
            loading.showLoading(true);
            Api.stationDetails(false, callback);
            switchFragment(StationsFragment.newInstance());
        });

        bottom_sheet = findViewById(R.id.bottom_sheet);
        behavior = ViewPagerBottomSheetBehavior.from(bottom_sheet);

        switchFragment(StationsFragment.newInstance());

    }


    @Override
    public void onBackPressed() {

        if (dl.isDrawerOpen(GravityCompat.START)) {
            dl.closeDrawer(GravityCompat.START);
            return;
        }

        if (behavior.getState() == ViewPagerBottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(ViewPagerBottomSheetBehavior.STATE_COLLAPSED);
        } else {
            fragments.pop();
            try {
                Fragment f = fragments.pop();
                switchFragment(f);
            } catch (EmptyStackException e) {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        //mMap.setTrafficEnabled(true);
        setupClusterManager();
        mMap.setPadding(12, 200, 12, behavior.getPeekHeight());
        if (MapUtility.checkLocationPermission(this)) {
            location_icon.setOnClickListener(v -> fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    if (location != null)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15f));
                }
            }));
            mMap.setMyLocationEnabled(true);
        }

        if (ViewGroupUtils.isDarkTheme(this))
            mMap.setMapStyle(new MapStyleOptions(getString(R.string.dark_2)));
        else
            mMap.setMapStyle(new MapStyleOptions(getString(R.string.white)));

        mMap.setOnInfoWindowClickListener(marker -> {
            Intent i = new Intent(this, StationActivity.class);
            Station station = (Station) marker.getTag();
            i.putExtra(StationActivity.STATION, station);
            startActivity(i);
        });

        // Set camera to Ljubljana
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ljubljana, 11.5f));

        Api.stationDetails(false, callback);


    }


    private void setupClusterManager() {
        clusterManager = new ClusterManager<>(this, mMap);
        CustomClusterRenderer customClusterRenderer = new CustomClusterRenderer(this, mMap, clusterManager);
        clusterManager.setRenderer(customClusterRenderer);
        clusterManager.setOnClusterClickListener(cluster -> {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cluster.getPosition(), mMap.getCameraPosition().zoom + 2f));
            return true;
        });
        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onStationsUpdated(List<Station> stations, boolean success, int responseCode) {

        if (success) {
            runOnUiThread(() -> {
                loading.showLoading(false);
                if (mMap != null) {
                    mMap.clear();
                    mMap.setInfoWindowAdapter(new StationInfoWindow(this));
                    if (clusterManager != null) {
                        clusterManager.clearItems();
                        for (Station station : stations)
                            clusterManager.addItem(new StationMarker(station.getLatitude(), station.getLongitude(), station));
                        clusterManager.cluster();
                    }
                }

            });
        } else {
            runOnUiThread(() -> {
                loading.showMsgDefault(this, responseCode);
            });
        }

    }

    @Override
    public void onTabClicked() {
        behavior.setState(ViewPagerBottomSheetBehavior.STATE_EXPANDED);
    }


    private void animateMarkerAlpha(final Marker marker) {
        final Handler handler = new Handler();

        final long startTime = SystemClock.uptimeMillis();
        final long duration = 300; // ms

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                marker.setAlpha(t);

                if (t < 1.0) {
                    // Post again 16ms later (60fps)
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    @Override
    public void onButtonPressed(int b) {
        switch (b) {
            case BUS:
                switchFragment(StationsFragment.newInstance());
                behavior.setState(ViewPagerBottomSheetBehavior.STATE_EXPANDED);
                break;
            case TRAIN:
            case BIKE:
            case PARKING:
            default:
                Log.i("MainActivity", b + " is not yet implemented!");
        }
    }

    private void switchFragment(Fragment fragment) {

        shadow.setVisibility(fragment instanceof HomeFragment ?View.GONE :View.VISIBLE);
        if (!(fragment instanceof HomeFragment)) {
            if (mMap != null)
                mMap.clear();
        } else if (mMap != null) {
            mMap.clear();
            markerManager = new BusMarkerManager(mMap, new MarkerOptions().icon(MapUtility.getMarkerIconFromDrawable(ContextCompat.getDrawable(this, R.drawable.bus_pointer_circle))).anchor(0.5f, 0.5f));
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.bottom_sheet,fragment);
        transaction.commit();
        fragments.push(fragment);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case locationRequestCode: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.forum:
                startActivity(new Intent(this, ForumActivity.class));
                break;
            case R.id.settings:
                startActivityForResult(new Intent(this, SettingsActivity.class), 0);
                break;
        }

        return true;
    }

}
