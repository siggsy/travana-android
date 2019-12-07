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
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.ApiCallback;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Station;
import com.VegaSolutions.lpptransit.ui.custommaps.CustomClusterRenderer;
import com.VegaSolutions.lpptransit.ui.custommaps.LocationMarkerManager;
import com.VegaSolutions.lpptransit.ui.custommaps.MyLocationManager;
import com.VegaSolutions.lpptransit.ui.custommaps.StationInfoWindow;
import com.VegaSolutions.lpptransit.ui.custommaps.StationMarker;
import com.VegaSolutions.lpptransit.ui.errorhandlers.TopMessage;
import com.VegaSolutions.lpptransit.ui.fragments.HomeFragment;
import com.VegaSolutions.lpptransit.ui.fragments.StationsFragment;
import com.VegaSolutions.lpptransit.utility.MapUtility;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.maps.android.clustering.ClusterManager;

import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

import biz.laenger.android.vpbs.ViewPagerBottomSheetBehavior;

public class MainActivity extends MapFragmentActivity implements StationsFragment.StationsFragmentListener, HomeFragment.HomeFragmentListener, NavigationView.OnNavigationItemSelectedListener{

    private final int locationRequestCode = 1000;

    // Fragment navigation stack
    Stack<Fragment> fragments = new Stack<>();

    // UI elements
    ImageButton navBarBtn, search;
    View shadow;
    TopMessage loading;
    ViewPagerBottomSheetBehavior behavior;
    DrawerLayout dl;
    NavigationView nv;
    View bottomSheet;

    private ClusterManager<StationMarker> clusterManager;

    // Map updater
    private ApiCallback<List<Station>> callback = (apiResponse, statusCode, success) -> {
        if (success) onStationsUpdated(apiResponse.getData(), true, statusCode);
        else onStationsUpdated(null, false, statusCode);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ViewGroupUtils.isDarkTheme(this) ? R.style.DarkTheme : R.style.WhiteTheme);
        setContentView(R.layout.app_nav_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Check for permission.
        if (!MapUtility.checkLocationPermission(this))
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, locationRequestCode);

        // Find all UI elements.
        dl = findViewById(R.id.nav_layout);
        nv = findViewById(R.id.nv);
        search = findViewById(R.id.search);
        navBarBtn = findViewById(R.id.account);
        locationIcon = findViewById(R.id.maps_location_icon);
        shadow = findViewById(R.id.shadow);
        loading = findViewById(R.id.top_message);
        bottomSheet = findViewById(R.id.bottom_sheet);

        // Setup UI elements.
        nv.setNavigationItemSelectedListener(this);

        search.setOnClickListener(view -> startActivity(new Intent(this, SearchActivity.class)));
        navBarBtn.setOnClickListener(view -> dl.openDrawer(GravityCompat.START));

        locationIcon.setVisibility(MapUtility.checkLocationPermission(this)? View.VISIBLE : View.GONE);

        loading.showLoading(true);
        loading.setErrorMsgBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
        loading.setErrorMsgColor(Color.WHITE);
        loading.setErrorIconColor(Color.WHITE);
        loading.setRefreshClickEvent(v -> {
            loading.showLoading(true);
            Api.stationDetails(false, callback);
            switchFragment(StationsFragment.newInstance());
        });

        behavior = ViewPagerBottomSheetBehavior.from(bottomSheet);

        // Switch bottom sheet fragment.
        switchFragment(StationsFragment.newInstance());

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);

        // Setup google maps UI.
        mMap.setPadding(12, 200, 12, behavior.getPeekHeight());
        setupClusterManager();

        // Set Station InfoWindow click listener.
        mMap.setOnInfoWindowClickListener(marker -> {
            Intent i = new Intent(this, StationActivity.class);
            Station station = (Station) marker.getTag();
            i.putExtra(StationActivity.STATION, station);
            startActivity(i);
        });

        // Query for stations.
        Api.stationDetails(false, callback);

    }

    @Override
    public void onBackPressed() {

        // Close drawer if open.
        if (dl.isDrawerOpen(GravityCompat.START)) {
            dl.closeDrawer(GravityCompat.START);
            return;
        }

        // Collapse bottom sheet if expanded.
        if (behavior.getState() == ViewPagerBottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(ViewPagerBottomSheetBehavior.STATE_COLLAPSED);
        } else {
            fragments.pop();

            // Go to previous fragment.
            try {
                Fragment f = fragments.pop();
                switchFragment(f);
            }

            // Close app if there is no previous fragment.
            catch (EmptyStackException e) {
                super.onBackPressed();
            }
        }
    }


    private void setupClusterManager() {

        clusterManager = new ClusterManager<>(this, mMap);
        CustomClusterRenderer customClusterRenderer = new CustomClusterRenderer(this, mMap, clusterManager);
        clusterManager.setRenderer(customClusterRenderer);

        // Set cluster expand animation.
        clusterManager.setOnClusterClickListener(cluster -> {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cluster.getPosition(), mMap.getCameraPosition().zoom + 2f));
            return true;
        });

        // Set cluster manager as camera listener.
        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Restart activity if theme was changed.
        if (requestCode == 0) {
            if (resultCode == SettingsActivity.SETTINGS_UPDATE) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        }
    }

    @Override
    public void onStationsUpdated(List<Station> stations, boolean success, int responseCode) {
        runOnUiThread(() -> {

            if (success) {
                loading.showLoading(false);
                if (mMap != null) {

                    // Clear map and add station markers
                    // mMap.clear(); // TODO: (fix) Removes current location
                    mMap.setInfoWindowAdapter(new StationInfoWindow(this));

                    // Refresh clusters
                    if (clusterManager != null) {
                        clusterManager.clearItems();
                        for (Station station : stations)
                            clusterManager.addItem(new StationMarker(station.getLatitude(), station.getLongitude(), station));
                        clusterManager.cluster();
                    }
                }
            }

            // Show error message on error
            else loading.showMsgDefault(this, responseCode);

        });

    }

    @Override
    public void onTabClicked() {
        behavior.setState(ViewPagerBottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onButtonPressed(int b) {

        // Old Home fragment interface (not in use ATM)

        /*switch (b) {
            case BUS:
                switchFragment(StationsFragment.newInstance());
                behavior.setState(ViewPagerBottomSheetBehavior.STATE_EXPANDED);
                break;
            case TRAIN:
            case BIKE:
            case PARKING:
            default:
                Log.i("MainActivity", b + " is not yet implemented!");
        }*/

    }

    private void switchFragment(Fragment fragment) {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.bottom_sheet,fragment);
        transaction.commit();
        fragments.push(fragment);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == locationRequestCode) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mMap != null)
                    onMapReady(mMap);
            } else Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        // Side drawer interaction
        switch (id) {
            case R.id.settings:
                startActivityForResult(new Intent(this, SettingsActivity.class), 0);
                break;
            case R.id.forum:
                startActivity(new Intent(this, ForumActivity.class));
        }

        return true;
    }

}
