package com.VegaSolutions.lpptransit.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.Toast;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.ApiCallback;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.ApiResponse;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Bus;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Station;
import com.VegaSolutions.lpptransit.ui.custommaps.BusMarkerManager;
import com.VegaSolutions.lpptransit.ui.custommaps.CustomClusterRenderer;
import com.VegaSolutions.lpptransit.ui.custommaps.StationInfoWindow;
import com.VegaSolutions.lpptransit.ui.custommaps.StationMarker;
import com.VegaSolutions.lpptransit.ui.fragments.HomeFragment;
import com.VegaSolutions.lpptransit.ui.fragments.StationsFragment;
import com.VegaSolutions.lpptransit.ui.fragments.subfragments.StationsSubFragment;
import com.VegaSolutions.lpptransit.utility.MapUtility;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

import biz.laenger.android.vpbs.ViewPagerBottomSheetBehavior;

import static com.VegaSolutions.lpptransit.ui.fragments.HomeFragment.BIKE;
import static com.VegaSolutions.lpptransit.ui.fragments.HomeFragment.BUS;
import static com.VegaSolutions.lpptransit.ui.fragments.HomeFragment.PARKING;
import static com.VegaSolutions.lpptransit.ui.fragments.HomeFragment.TRAIN;


// TODO: Clean the code, fix MapPadding remove useless callbacks and variables

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, StationsFragment.StationsFragmentListener, HomeFragment.HomeFragmentListener {

    private GoogleMap mMap;

    LatLng ljubljana = new LatLng(46.056319, 14.505381);
    FusedLocationProviderClient fusedLocationProviderClient;
    private final int locationRequestCode = 1000;

    ViewPagerBottomSheetBehavior behavior;

    Stack<Fragment> fragments = new Stack<>();

    ImageButton account;
    ImageButton search;
    View shadow;
    View loading;

    View bottom_sheet;
    BusMarkerManager markerManager;
    private ClusterManager<StationMarker> clusterManager;

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
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    locationRequestCode);

        }

        search = findViewById(R.id.search);
        search.setOnClickListener(view -> startActivity(new Intent(this, SearchActivity.class)));
        account = findViewById(R.id.account);
        account.setOnClickListener(view -> startActivityForResult(new Intent(this, SettingsActivity.class), 0));
        shadow = findViewById(R.id.shadow);
        loading = findViewById(R.id.maps_progress_bar);
        loading.postOnAnimationDelayed(() -> loading.setVisibility(loading.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE), 200);



        bottom_sheet = findViewById(R.id.bottom_sheet);
        behavior = ViewPagerBottomSheetBehavior.from(bottom_sheet);
        behavior.setBottomSheetCallback(new ViewPagerBottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                if (newState == ViewPagerBottomSheetBehavior.STATE_DRAGGING) {
                    if (account.getVisibility() == View.VISIBLE) {
                        Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shrink);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                account.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        account.startAnimation(animation);
                    }

                } else if (newState == ViewPagerBottomSheetBehavior.STATE_COLLAPSED) {
                    Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.expand);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            account.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    account.startAnimation(animation);
                } else if (newState == ViewPagerBottomSheetBehavior.STATE_EXPANDED) {
                    account.setVisibility(View.GONE);
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        switchFragment(StationsFragment.newInstance());

    }


    @Override
    public void onBackPressed() {
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
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        //mMap.setTrafficEnabled(true);
        setupClusterManager();
        mMap.setPadding(12, 150, 12, behavior.getPeekHeight());
        mMap.setMyLocationEnabled(MapUtility.checkLocationPermission(this));

        if (ViewGroupUtils.isDarkTheme(this))
            mMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.dark)));

        mMap.setOnInfoWindowClickListener(marker -> {
            Intent i = new Intent(this, StationActivity.class);
            Station station = (Station) marker.getTag();
            i.putExtra(StationActivity.STATION, station);
            startActivity(i);
        });

        // Set camera to Ljubljana
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ljubljana, 11.5f));


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
    public void onStationsUpdated(List<Station> stations) {

        runOnUiThread(() -> {
            loading.setSelected(true);
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
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
