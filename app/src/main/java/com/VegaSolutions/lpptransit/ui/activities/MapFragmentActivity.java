package com.VegaSolutions.lpptransit.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.location.Location;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.TravanaApp;
import com.VegaSolutions.lpptransit.ui.custommaps.LocationMarkerManager;
import com.VegaSolutions.lpptransit.ui.custommaps.LocationProvider;
import com.VegaSolutions.lpptransit.ui.custommaps.LocationProviderListener;
import com.VegaSolutions.lpptransit.ui.custommaps.TravanaLocationManager;
import com.VegaSolutions.lpptransit.utility.MapUtility;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

import java.util.ArrayList;
import java.util.List;

public abstract class MapFragmentActivity extends AppCompatActivity implements OnMapReadyCallback, LocationProviderListener {

    protected GoogleMap mMap;

    protected LatLng ljubljana = new LatLng(46.056319, 14.505381);
    protected LocationProvider locationManager;
    protected LocationMarkerManager markerManager;

    protected ImageView locationIcon;

    protected List<View> toHide = new ArrayList<>();
    protected boolean hidden = false;

    private int paddingTop = 0;
    private int paddingBottom = 0;
    private int paddingLeft = 0;
    private int paddingRight = 0;

    @Override
    protected void onPause() {
        super.onPause();
        if (locationManager != null)
            locationManager.unsubscribe(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (locationManager != null)
            locationManager.subscribe(this, this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        toHide.add(locationIcon);

        // Disable buildings
        mMap.setBuildingsEnabled(false);

        // Setup generic UI
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        if (ViewGroupUtils.isDarkTheme(this)) {
            mMap.setMapStyle(new MapStyleOptions(getString(R.string.dark)));
        }

        // Setup location objects
        locationManager = LocationProvider.INSTANCE;
        markerManager = new LocationMarkerManager(this, mMap,
                MapUtility.getLatLngFromLocation(locationManager.getPrevLocation()),
                MapUtility.getMarkerIconFromDrawable(ContextCompat.getDrawable(this, R.drawable.current_location_live)),
                MapUtility.getMarkerIconFromDrawable(ContextCompat.getDrawable(this, R.drawable.current_location_offline)));

        markerManager.setLive(locationManager.isLive());

        locationIcon.setVisibility(View.VISIBLE);
        locationIcon.setOnClickListener(v -> {
            LatLng location = MapUtility.getLatLngFromLocation(locationManager.getPrevLocation());
            if (location != null)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
        });

        // Set location button location callback.
        if (MapUtility.checkIfAtLeastOnePermissionPermitted(this)) {
            locationManager.subscribe(this, this);
        }

    }

    private void show(View view, boolean value) {

        // Animate view alpha.
        if (!value) {
            // Show view.
            view.setVisibility(View.VISIBLE);
            animatePadding(true);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "alpha", 0, 1);
            objectAnimator.setDuration(200);
            objectAnimator.setInterpolator(new LinearInterpolator());
            objectAnimator.start();
        } else {
            // Hide view.
            animatePadding(false);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "alpha", 1, 0);
            objectAnimator.setDuration(200);
            objectAnimator.setInterpolator(new LinearInterpolator());
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    view.setVisibility(View.GONE);
                }
            });
            objectAnimator.start();
        }
    }

    private void animatePadding(boolean value) {

        ValueAnimator animator;
        if (value) {
            animator = ValueAnimator.ofInt(0, paddingBottom);
        } else {
            animator = ValueAnimator.ofInt(paddingBottom, 0);
        }
        animator.setDuration(200);

        animator.addUpdateListener(animation -> {
            int value1 = (int) animation.getAnimatedValue();
            mMap.setPadding(paddingLeft, paddingTop, paddingRight, value1);
        });

        animator.start();

    }

    protected void setPadding(int left, int top, int right, int bottom) {
        mMap.setPadding(left, top, right, bottom);
        paddingBottom = bottom;
        paddingLeft = left;
        paddingTop = top;
        paddingRight = right;
    }

    protected void setBottomPadding(int bottom) {
        if (mMap != null)
            mMap.setPadding(paddingLeft, paddingTop, paddingRight, bottom);
        paddingBottom = bottom;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        runOnUiThread(() -> {
            if (markerManager != null)
                markerManager.update(location);
        });
    }

    @Override
    public void onAvailabilityChanged(boolean isLive) {
        runOnUiThread(() -> {
            if (markerManager != null)
                markerManager.setLive(isLive);
        });
    }

}
