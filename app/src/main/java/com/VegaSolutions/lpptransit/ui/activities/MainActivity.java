package com.VegaSolutions.lpptransit.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.TravanaApp;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Station;
import com.VegaSolutions.lpptransit.ui.animations.ElevationAnimation;
import com.VegaSolutions.lpptransit.ui.custommaps.CustomClusterRenderer;
import com.VegaSolutions.lpptransit.ui.custommaps.StationInfoWindow;
import com.VegaSolutions.lpptransit.ui.custommaps.StationMarker;
import com.VegaSolutions.lpptransit.ui.errorhandlers.TopMessage;
import com.VegaSolutions.lpptransit.ui.fragments.FragmentHeaderCallback;
import com.VegaSolutions.lpptransit.ui.fragments.lpp.StationsFragment;
import com.VegaSolutions.lpptransit.utility.Constants;
import com.VegaSolutions.lpptransit.utility.MapUtility;
import com.VegaSolutions.lpptransit.utility.NetworkConnectivityManager;
import com.VegaSolutions.lpptransit.utility.ScreenState;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

import biz.laenger.android.vpbs.ViewPagerBottomSheetBehavior;

public class  MainActivity extends MapFragmentActivity implements StationsFragment.StationsFragmentListener, FragmentHeaderCallback {

    public static final String TAG = "MainActivity";

    StationsFragment stationsFragment;

    // UI elements
    ImageButton navBarBtn, search;
    View shadow;
    TopMessage loading;
    ViewPagerBottomSheetBehavior behavior;
    DrawerLayout dl;
    NavigationView nv;
    View bottomSheet;
    CardView header;
    GoogleMap googleMap;
    View mapFilter;
    View bottom;
    RelativeLayout detoursRl;
    RelativeLayout newsRl;
    RelativeLayout settingsRl;
    RelativeLayout aboutRl;
    int bottomTopMargin = 0;
    int headerTopMargin = 0;

    ElevationAnimation elevationAnimation;

    LatLng lastValidMapCenter = ljubljana;

    private TravanaApp app;
    private ClusterManager<StationMarker> clusterManager;
    private NetworkConnectivityManager networkConnectivityManager;

    public ScreenState screenState = ScreenState.LOADING;
    public int errorCode = NetworkConnectivityManager.NO_ERROR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.app_nav_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        app = TravanaApp.getInstance();
        networkConnectivityManager = app.getNetworkConnectivityManager();

        setScreenSettings();
        initElements();
        setStationsFragment();

        // Check for permission.
        if (!MapUtility.checkIfAtLeastOnePermissionPermitted(this)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.LOCATION_REQUEST_CODE);
        }

        retrieveStations();

    }

    private void setStationsFragment() {
        stationsFragment = new StationsFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.bottom_sheet, stationsFragment);
        transaction.commit();
    }

    private void initElements() {
        // Find all UI elements.
        dl = findViewById(R.id.nav_layout);
        nv = findViewById(R.id.nv);
        search = findViewById(R.id.search);
        navBarBtn = findViewById(R.id.account);
        locationIcon = findViewById(R.id.maps_location_icon);
        shadow = findViewById(R.id.shadow);
        loading = findViewById(R.id.top_message);
        bottomSheet = findViewById(R.id.bottom_sheet);
        header = findViewById(R.id.header);
        bottom = findViewById(R.id.bottom_main);
        mapFilter = findViewById(R.id.map_filter);
        detoursRl = findViewById(R.id.rl_detour);
        newsRl = findViewById(R.id.rl_news);
        settingsRl = findViewById(R.id.rl_settings);
        aboutRl = findViewById(R.id.rl_about);

        elevationAnimation = new ElevationAnimation(16, null, header, mapFilter);

        ViewGroup.MarginLayoutParams bottomParams = (ViewGroup.MarginLayoutParams) bottom.getLayoutParams();
        bottomTopMargin = bottomParams.topMargin;
        ViewGroup.MarginLayoutParams headerParams = (ViewGroup.MarginLayoutParams) header.getLayoutParams();
        headerTopMargin = headerParams.topMargin;

        toHide.add(bottomSheet);
        toHide.add(header);
        toHide.add(shadow);


        detoursRl.setOnClickListener(view -> {
            startActivity(new Intent(this, DetourActivity.class));
        });
        newsRl.setOnClickListener(view -> {
            Intent i = new Intent(this, WebViewActivity.class);
            i.putExtra(Constants.LINK_KEY, this.getResources().getString(R.string.lpp_news_webside));
            startActivity(i);
        });
        settingsRl.setOnClickListener(view -> {
            startActivityForResult(new Intent(this, SettingsActivity.class), 0);
        });
        aboutRl.setOnClickListener(view -> {
            startActivity(new Intent(this, AboutActivity.class));
        });

        // Setup UI elements.

        search.setOnClickListener(view -> startActivity(new Intent(this, SearchActivity.class)));
        navBarBtn.setOnClickListener(view -> dl.openDrawer(GravityCompat.START));

        setUpTopMessageSettings();

        behavior = ViewPagerBottomSheetBehavior.from(bottomSheet);
        behavior.setState(ViewPagerBottomSheetBehavior.STATE_EXPANDED);

        float headerElevation = 12f;
        behavior.setBottomSheetCallback(new ViewPagerBottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                if (newState == ViewPagerBottomSheetBehavior.STATE_EXPANDED) {
                    header.setElevation(0);
                } else if (newState == ViewPagerBottomSheetBehavior.STATE_COLLAPSED) {
                    mapFilter.setAlpha(0f);
                    header.setElevation(headerElevation);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                switch (behavior.getState()) {
                    case ViewPagerBottomSheetBehavior.STATE_DRAGGING:
                    case ViewPagerBottomSheetBehavior.STATE_SETTLING:
                        setMapPaddingBottom(slideOffset);
                        mapFilter.setAlpha(slideOffset);
                        header.setElevation((1 - slideOffset) * headerElevation);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(lastValidMapCenter));
                        break;
                }
            }
        });
    }

    // set up topMessage - loading, error, done...
    private void setUpMainActivityUi(ScreenState screenState) {
        this.screenState = screenState;
        runOnUiThread(() -> {
            switch (screenState) {
                case DONE: {
                    loading.showLoading(false);
                    break;
                }
                case LOADING: {
                    loading.showLoading(true);
                    break;
                }
                case ERROR: {
                    loading.showMsgDefault(this, errorCode);
                    break;
                }
            }
        });
    }

    private void setUpTopMessageSettings() {
        loading.setErrorMsgBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
        loading.setErrorMsgColor(Color.WHITE);
        loading.setErrorIconColor(Color.WHITE);
        loading.setRefreshClickEvent(v -> {
            retrieveStations();
        });
    }

    private void setScreenSettings() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | (ViewGroupUtils.isDarkTheme(this) ?
                        0 : View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR));
            } else {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | (ViewGroupUtils.isDarkTheme(this) ?
                        0 : View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR));
            }
        } else {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        window.setStatusBarColor(Color.TRANSPARENT);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root), (i, insets) -> {
            ViewGroup.MarginLayoutParams headerParams = (ViewGroup.MarginLayoutParams) header.getLayoutParams();
            ViewGroup.MarginLayoutParams bottomParams = (ViewGroup.MarginLayoutParams) bottom.getLayoutParams();
            headerParams.setMargins(0, headerTopMargin + insets.getSystemWindowInsetTop(), 0, 0);
            bottomParams.setMargins(0, bottomTopMargin + insets.getSystemWindowInsetTop(), 0, 0);
            header.setLayoutParams(headerParams);
            bottom.setLayoutParams(bottomParams);
            return insets.consumeSystemWindowInsets();
        });
    }

    public void retrieveStations() {

        if (!networkConnectivityManager.isConnectionAvailable()) {
            errorCode = NetworkConnectivityManager.NO_INTERNET_CONNECTION;
            setUpMainActivityUi(ScreenState.ERROR);
            stationsFragment.setSubstationsFragments();
            return;
        }

        // return if stations are already loaded
        if (app.areStationsLoaded()) {
            screenState = ScreenState.DONE;
            errorCode = NetworkConnectivityManager.NO_ERROR;
            return;
        }
        setUpMainActivityUi(ScreenState.LOADING);
        stationsFragment.setSubstationsFragments();

        Api.stationDetails(false, (apiResponse, statusCode, success) -> {
            if (success) {
                app.setStations((ArrayList<Station>) apiResponse.getData());
                runOnUiThread(() -> {
                    setUpStationMarkers();
                });
                setUpMainActivityUi(ScreenState.DONE);
                errorCode = NetworkConnectivityManager.NO_ERROR;
            } else {
                setUpMainActivityUi(ScreenState.ERROR);
                errorCode = NetworkConnectivityManager.ERROR_DURING_LOADING;
            }
            stationsFragment.setSubstationsFragments();
        });
    }

    private void setUpStationMarkers() {
        Log.e(TAG, "Setting markers 1.2");
        Log.e(TAG, "Setting markers mMap is null " + (mMap == null));
        Log.e(TAG, "Setting markers googleMaps is null " + (this.googleMap == null));
        if (mMap != null) {
            mMap.setInfoWindowAdapter(new StationInfoWindow(this));

            Log.e(TAG, "Setting markers 2");

            // Refresh clusters
            if (clusterManager != null) {
                clusterManager.clearItems();
                for (Station station : app.getStations())
                    clusterManager.addItem(new StationMarker(station.getLatitude(), station.getLongitude(), station));
                clusterManager.cluster();

                Log.e(TAG, "Setting markers 3");
            }
        }
    }

    private void setMapPaddingBottom(Float offset) {
        float maxMapPaddingBottom = (float) behavior.getPeekHeight();

        if (googleMap != null) {
            setPadding(0, 0, 0, Math.round(offset * maxMapPaddingBottom) + (int) maxMapPaddingBottom);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "Setting markers 1");
        setUpStationMarkers();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);

        // Setup google maps UI.
        this.googleMap = googleMap;

        setupClusterManager();

        setPadding(12, 200, 12, behavior.getPeekHeight());
        setMapPaddingBottom(0f);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ljubljana, 11.5f));

        // Set Station InfoWindow click listener.
        mMap.setOnInfoWindowClickListener(marker -> {
            Intent i = new Intent(this, StationActivity.class);
            Station station = (Station) marker.getTag();
            i.putExtra(StationActivity.STATION, station);
            startActivity(i);
        });

        mMap.setOnCameraMoveListener(() -> lastValidMapCenter = mMap.getCameraPosition().target);

    }

    @Override
    public void onBackPressed() {

        // Close drawer if open.
        if (dl.isDrawerOpen(GravityCompat.START)) {
            dl.closeDrawer(GravityCompat.START);
            return;
        }

        // Collapse bottom sheet if expanded.
        if (behavior.getState() == ViewPagerBottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(ViewPagerBottomSheetBehavior.STATE_EXPANDED);
        } else {
            super.onBackPressed();
        }
    }


    private void setupClusterManager() {

        if (clusterManager == null) {
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
        //Nothing
    }

    @Override
    public void onTabClicked() {
        behavior.setState(ViewPagerBottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.LOCATION_REQUEST_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                boolean showRationale = shouldShowRequestPermissionRationale(permissions[0]);
                if (!showRationale) {
                    Toast.makeText(this, getResources().getString(R.string.please_enable_location_permission_in_the_settings), Toast.LENGTH_LONG).show();
                    return;
                }
            }

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mMap != null) {
                    onMapReady(mMap);
                }
                stationsFragment.setSubstationsFragments();         // reload list of nearby stations when users enables location in the app
            }
        }
    }

    @Override
    public void onHeaderChanged(boolean selected) {
        elevationAnimation.elevate(selected);
    }
}
