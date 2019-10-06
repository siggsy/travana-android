package com.VegaSolutions.lpptransit.ui.activities;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.animators.MapAnimator;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.ApiCallback;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.ApiResponse;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Bus;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Station;
import com.VegaSolutions.lpptransit.test.LiveArrival;
import com.VegaSolutions.lpptransit.ui.bottomfragments.MainFragment;
import com.VegaSolutions.lpptransit.lppapideprecated.responseclasses.StationsInRange;
import com.VegaSolutions.lpptransit.ui.viewmodels.LppSharedViewModel;
import com.VegaSolutions.lpptransit.ui.viewmodels.LppViewModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MainActivity extends FragmentActivity implements
        OnMapReadyCallback,
        MapAnimator.MapAnimatorListener,
        MainFragment.OnFragmentInteractionListener {

    private GoogleMap mMap;
    private MapAnimator mapAnimator;
    private LppViewModel model;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Get data manager.
        mapAnimator = new MapAnimator(this, mMap);
        model = ViewModelProviders.of(this).get(LppViewModel.class);
        model.getStations().observe(this, stations -> {
            for (Station station : stations)
                mMap.addMarker(new MarkerOptions().position(new LatLng(station.getLatitude(), station.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.location_pin_36)).title(station.getName()).snippet(station.getRef_id()));
        });
        switchFragment(MainFragment.newInstance());


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(marker -> {
            Intent i = new Intent(MainActivity.this, LiveArrival.class);
            i.putExtra("station_id", marker.getSnippet());
            startActivity(i);
        });
        model.getStationsInRage();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onRemoveAnimationFinished(Marker marker) {

    }

    private void switchFragment(Fragment fragment) {

        FragmentManager manager = this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.replace(R.id.bottom_sheet_fragment, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onFragmentInteraction(int uri) {

    }
}
