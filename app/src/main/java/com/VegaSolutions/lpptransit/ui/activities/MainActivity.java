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
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Station;
import com.VegaSolutions.lpptransit.ui.fragments.HomeFragment;
import com.VegaSolutions.lpptransit.ui.fragments.StationsFragment;
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
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.List;
import java.util.Queue;
import java.util.Stack;

import static com.VegaSolutions.lpptransit.ui.fragments.HomeFragment.BIKE;
import static com.VegaSolutions.lpptransit.ui.fragments.HomeFragment.BUS;
import static com.VegaSolutions.lpptransit.ui.fragments.HomeFragment.PARKING;
import static com.VegaSolutions.lpptransit.ui.fragments.HomeFragment.TRAIN;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, StationsFragment.StationsFragmentListener, HomeFragment.HomeFragmentListener {

    private GoogleMap mMap;

    LatLng ljubljana = new LatLng(46.056319, 14.505381);
    CameraUpdate default_camera = CameraUpdateFactory.newLatLngZoom(ljubljana, 11.5f);

    BottomSheetBehavior behavior;

    Stack<Fragment> fragments = new Stack<>();

    ImageButton account;
    ImageButton search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        search = findViewById(R.id.search);
        search.setOnClickListener(view -> startActivity(new Intent(this, SearchActivity.class)));

        account = findViewById(R.id.account);
        account.setOnClickListener(view -> startActivity(new Intent(this, SignInActivity.class)));

        View a = findViewById(R.id.bottom_sheet);

        switchFragment(HomeFragment.newInstance());

        behavior = BottomSheetBehavior.from(a);
        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
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

                }
                else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
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
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
        });

    }

    @Override
    public void onBackPressed() {
        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            Fragment f = fragments.pop();
            if (f != null) switchFragment(f);
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            fragments.pop();
            Fragment f = fragments.pop();
            if (f != null) switchFragment(f);
            else super.onBackPressed();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnInfoWindowClickListener(marker -> {
            Intent i = new Intent(this, StationActivity.class);
            i.putExtra("station_code", marker.getSnippet());
            i.putExtra("station_name", marker.getTitle());
            i.putExtra("station_center", Integer.valueOf(marker.getSnippet()) % 2 != 0);
            startActivity(i);
        });

        // Set camera to ljubljana

        mMap.moveCamera(default_camera);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onStationsUpdated(List<Station> stations) {
        runOnUiThread(() -> mMap.clear());
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Station station : stations) {
            builder.include(station.getLatLng());
            runOnUiThread(() -> {
                Marker m = mMap.addMarker(new MarkerOptions().position(station.getLatLng()).alpha(0f).title(station.getName()).snippet(station.getRef_id()));
                animateMarkerAlpha(m);
            });
        }
        if (stations.size() > 0) {
            LatLngBounds bounds = builder.build();
            runOnUiThread(() -> mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150)));
        } else {
            runOnUiThread(() -> mMap.animateCamera(default_camera));
        }
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
            default: Log.i("MainActivity", b + " is not yet implemented!");
        }
    }

    private void switchFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.bottom_sheet, fragment);
        transaction.commit();
        fragments.push(fragment);
    }

}
