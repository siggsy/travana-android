package com.VegaSolutions.lpptransit.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.location.Location;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.ui.custommaps.LocationMarkerManager;
import com.VegaSolutions.lpptransit.ui.custommaps.TravanaLocationManager;
import com.VegaSolutions.lpptransit.utility.MapUtility;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

import java.util.ArrayList;
import java.util.List;

public abstract class MapFragmentActivity extends FragmentActivity implements OnMapReadyCallback, TravanaLocationManager.TravanaLocationListener {

    protected GoogleMap mMap;

    protected LatLng ljubljana = new LatLng(46.056319, 14.505381);
    protected TravanaLocationManager locationManager;
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
            locationManager.removeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (locationManager != null)
            locationManager.addListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        toHide.add(locationIcon);

        // Setup generic UI
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.setMapStyle(new MapStyleOptions(ViewGroupUtils.isDarkTheme(this) ? getString(R.string.dark_2) : getString(R.string.white)));

        // Set location button location callback.
        if (MapUtility.checkIfAtLeastOnePermissionPermitted(this)) {

            // Setup location objects
            locationManager = new TravanaLocationManager(this);
            markerManager = new LocationMarkerManager(this, mMap,
                    locationManager.getLatest(),
                    MapUtility.getMarkerIconFromDrawable(ContextCompat.getDrawable(this, R.drawable.current_location_live)),
                    MapUtility.getMarkerIconFromDrawable(ContextCompat.getDrawable(this, R.drawable.current_location_offline)));

            markerManager.setLive(locationManager.isLive());
            locationManager.addListener(this);

            locationIcon.setVisibility(View.VISIBLE);
            locationIcon.setOnClickListener(v -> {
                LatLng location = locationManager.getLatest();
                if (location != null)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
            });

        }

        // remove this option.
        //mMap.setOnMapClickListener(latLng -> setHide(!hidden));


    }

    private void setHide(boolean value) {
        for (View v : toHide)
            show(v, value);
        hidden = !hidden;
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
    public void onLocationChanged(Location location) {
        if (markerManager != null)
            markerManager.update(location);
    }

    @Override
    public void onProviderAvailabilityChanged(boolean value) {
        if (markerManager != null)
            markerManager.setLive(value);
    }

}
