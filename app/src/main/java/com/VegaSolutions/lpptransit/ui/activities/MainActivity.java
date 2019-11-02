package com.VegaSolutions.lpptransit.ui.activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Station;
import com.VegaSolutions.lpptransit.ui.custommaps.CustomClusterRenderer;
import com.VegaSolutions.lpptransit.ui.custommaps.StationInfoWindow;
import com.VegaSolutions.lpptransit.ui.custommaps.StationMarker;
import com.VegaSolutions.lpptransit.ui.fragments.FragmentLifecycleListener;
import com.VegaSolutions.lpptransit.ui.fragments.HomeFragment;
import com.VegaSolutions.lpptransit.ui.fragments.StationsFragment;
import com.VegaSolutions.lpptransit.ui.fragments.subfragments.StationsSubFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.maps.android.clustering.ClusterManager;

import java.util.EmptyStackException;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import biz.laenger.android.vpbs.ViewPagerBottomSheetBehavior;

import static com.VegaSolutions.lpptransit.ui.fragments.HomeFragment.BIKE;
import static com.VegaSolutions.lpptransit.ui.fragments.HomeFragment.BUS;
import static com.VegaSolutions.lpptransit.ui.fragments.HomeFragment.PARKING;
import static com.VegaSolutions.lpptransit.ui.fragments.HomeFragment.TRAIN;


// TODO: Clean the code, fix MapPadding remove useless callbacks and variables

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, StationsSubFragment.StationsFragmentListener, HomeFragment.HomeFragmentListener, FragmentLifecycleListener {

    private GoogleMap mMap;

    LatLng ljubljana = new LatLng(46.056319, 14.505381);

    ViewPagerBottomSheetBehavior behavior;

    Stack<Fragment> fragments = new Stack<>();

    ImageButton account;
    ImageButton search;
    View shadow;
    View root;

    View bottom_sheet;
    int maxMapsPadding = 0;
    int minMapsPadding = 0;
    int previousTop = 0;

    private ClusterManager<StationMarker> clusterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        maxMapsPadding = displayMetrics.heightPixels;
        getWindow().getDecorView().getHeight();

        root = findViewById(R.id.root);
        maxMapsPadding = root.getHeight();
        Log.i("root", getWindow().getDecorView().getHeight() + "");

        search = findViewById(R.id.search);
        search.setOnClickListener(view -> startActivity(new Intent(this, SearchActivity.class)));
        account = findViewById(R.id.account);
        account.setOnClickListener(view -> startActivity(new Intent(this, SignInActivity.class)));
        shadow = findViewById(R.id.shadow);


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
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                if (slideOffset < 1)
                    setMapPadding(slideOffset);
                else setMapPadding(0);
                Log.i("Maps", "offset -> " + slideOffset);

            }
        });

        switchFragment(HomeFragment.newInstance());



    }

    @Override
    public void onBackPressed() {
        if (behavior.getState() == ViewPagerBottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(ViewPagerBottomSheetBehavior.STATE_COLLAPSED);
        } else {
            fragments.pop();
            try {
                Fragment f = fragments.pop();
                if (f != null) switchFragment(f);
                else super.onBackPressed();
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
        mMap.setTrafficEnabled(true);
        setupClusterManager();


        mMap.setOnInfoWindowClickListener(marker -> {
            Intent i = new Intent(this, StationActivity.class);
            Station station = (Station) marker.getTag();
            i.putExtra("station_code", station.getRef_id());
            i.putExtra("station_name", station.getName());
            i.putExtra("station_center", Integer.valueOf((station.getRef_id())) % 2 != 0);
            startActivity(i);
        });

        // Set camera to ljubljana
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ljubljana, 11.5f));

    }

    void setMapPadding(float offset) {
        if (mMap == null) return;
        offset *= (maxMapsPadding - 150) * .5;
        mMap.setPadding(12, 150, 12, Math.round((offset) + minMapsPadding));
    }

    private void setupClusterManager() {
        clusterManager = new ClusterManager<>(this, mMap);
        CustomClusterRenderer customClusterRenderer = new CustomClusterRenderer(this, mMap, clusterManager);
        customClusterRenderer.setMinClusterSize(10);
        clusterManager.setRenderer(customClusterRenderer);
        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onStationsUpdated(List<Station> stations) {
        runOnUiThread(() -> {
            if (mMap != null) {
                mMap.clear();
                mMap.setInfoWindowAdapter(new StationInfoWindow(this));
            }
            if (clusterManager != null) clusterManager.clearItems();
        });

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        if (stations.size() > 300) {
            for (Station station : stations) {
                builder.include(station.getLatLng());
                runOnUiThread(() -> clusterManager.addItem(new StationMarker(station.getLatitude(), station.getLongitude(), station)));
            }
            runOnUiThread(() -> clusterManager.cluster());
            return;
        }

        VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
        boolean updateNeeded = true;
        for (Station station : stations) {
            builder.include(station.getLatLng());
            if (visibleRegion.latLngBounds.contains(station.getLatLng())) updateNeeded = false;
            runOnUiThread(() -> {
                Marker m = mMap.addMarker(new MarkerOptions().position(station.getLatLng()).alpha(0f));
                m.setTag(station);
                animateMarkerAlpha(m);
            });
        }
        if (stations.size() > 0) {
            LatLngBounds bounds = builder.build();
            if (updateNeeded)
                runOnUiThread(() -> mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150)));
        } else
            runOnUiThread(() -> mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ljubljana, 11.5f)));
    }

    private void animateMarkerPosition(final Marker marker) {
        final Handler handler = new Handler();

        final long startTime = SystemClock.uptimeMillis();
        final long duration = 300; // ms

        Projection proj = mMap.getProjection();
        final LatLng markerLatLng = marker.getPosition();
        Point startPoint = proj.toScreenLocation(markerLatLng);
        startPoint.offset(0, -10);
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);

        final Interpolator interpolator = new BounceInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * markerLatLng.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * markerLatLng.latitude + (1 - t) * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later (60fps)
                    handler.postDelayed(this, 16);
                }
            }
        });
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
                break;
            case TRAIN:
            case BIKE:
            case PARKING:
            default:
                Log.i("MainActivity", b + " is not yet implemented!");
        }
    }

    private void switchFragment(Fragment fragment) {

        bottom_sheet.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.i("Updated", "this many");
                if (previousTop != bottom_sheet.getTop()) {
                    previousTop = bottom_sheet.getTop();
                    bottom_sheet.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                minMapsPadding = (int) Math.round((maxMapsPadding - bottom_sheet.getTop()) - maxMapsPadding * 0.02);
                setMapPadding(0);
            }
        });

        maxMapsPadding = getWindow().getDecorView().getHeight() - 50;

        shadow.setVisibility(fragment instanceof HomeFragment ?View.GONE :View.VISIBLE);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.bottom_sheet,fragment);
        transaction.commit();
        fragments.push(fragment);
        fm.executePendingTransactions();


    }

    @Override
    public void fragmentOnResume() {

        Log.i("Updated", "onResume");
    }

    @Override
    public void fragmentOnPause() {

    }

    @Override
    public void fragmentOnCreated() {

    }

    @Override
    public void fragmentOnCreatedView() {

    }
}
