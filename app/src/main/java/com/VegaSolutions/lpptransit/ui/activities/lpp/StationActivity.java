package com.VegaSolutions.lpptransit.ui.activities.lpp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Station;
import com.VegaSolutions.lpptransit.ui.activities.MapFragmentActivity;
import com.VegaSolutions.lpptransit.ui.animations.ElevationAnimation;
import com.VegaSolutions.lpptransit.ui.custommaps.StationInfoWindow;
import com.VegaSolutions.lpptransit.ui.errorhandlers.CustomToast;
import com.VegaSolutions.lpptransit.ui.fragments.FragmentHeaderCallback;
import com.VegaSolutions.lpptransit.ui.fragments.lpp.LiveArrivalFragment;
import com.VegaSolutions.lpptransit.ui.fragments.lpp.RoutesOnStationFragment;
import com.VegaSolutions.lpptransit.utility.LppHelper;
import com.VegaSolutions.lpptransit.utility.MapUtility;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;

import biz.laenger.android.vpbs.ViewPagerBottomSheetBehavior;

public class StationActivity extends MapFragmentActivity implements FragmentHeaderCallback {

    public static final String TAG = "StationActivity";
    public static final String STATION = "station";

    // Views
    TextView name, center;
    FrameLayout header;
    ImageButton oppositeBtn;
    ImageView fav, back;
    ViewPager viewPager;
    TabLayout tabLayout;
    ViewPagerBottomSheetBehavior bottomSheetBehavior;

    LatLng lastValidMapCenter;

    View bottom;
    int bottomTopMargin = 0;

    View mapFilter;
    View shadow;

    Adapter adapter;

    // Station data
    Station station;
    boolean favourite;

    ElevationAnimation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ViewGroupUtils.isDarkTheme(this) ? R.style.DarkTheme : R.style.WhiteTheme);
        setContentView(R.layout.activity_station);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        View root = findViewById(R.id.rootConstraint);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | (ViewGroupUtils.isDarkTheme(this) ? 0 : View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR));
        } else {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        window.setStatusBarColor(Color.TRANSPARENT);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root), (i, insets) -> {
            ViewGroup.MarginLayoutParams bottomParams = (ViewGroup.MarginLayoutParams) bottom.getLayoutParams();
            bottomParams.setMargins(0, bottomTopMargin + insets.getSystemWindowInsetTop(), 0, 0);
            bottom.setLayoutParams(bottomParams);
            return insets.consumeSystemWindowInsets();
        });

        // Get bottom sheet behaviour for controlling expanding and collapsing
        bottomSheetBehavior = ViewPagerBottomSheetBehavior.from(root);
        bottomSheetBehavior.setState(ViewPagerBottomSheetBehavior.STATE_EXPANDED);

        bottomSheetBehavior.setBottomSheetCallback(new ViewPagerBottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                switch (bottomSheetBehavior.getState()) {
                    case ViewPagerBottomSheetBehavior.STATE_DRAGGING:
                    case ViewPagerBottomSheetBehavior.STATE_SETTLING:
                        setMapPaddingBottom(slideOffset);
                        mapFilter.setAlpha(slideOffset);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(lastValidMapCenter));
                        break;
                }
            }
        });


        // Assign all UI elements
        name = findViewById(R.id.station_title);
        header = findViewById(R.id.header);
        center = findViewById(R.id.station_center);
        oppositeBtn = findViewById(R.id.station_opposite_btn);
        fav = findViewById(R.id.station_favourite);
        viewPager = findViewById(R.id.station_pager);
        tabLayout = findViewById(R.id.station_tab_layout);
        back = findViewById(R.id.back);
        locationIcon = findViewById(R.id.maps_location_icon);
        shadow = findViewById(R.id.shadow);
        bottom = findViewById(R.id.bottom_station);
        mapFilter = findViewById(R.id.map_filter);

        ViewGroup.MarginLayoutParams bottomParams = (ViewGroup.MarginLayoutParams) bottom.getLayoutParams();
        bottomTopMargin = bottomParams.topMargin;

        toHide.add(root);
        toHide.add(shadow);

        // Get Intent data
        station = getIntent().getParcelableExtra(STATION);
        favourite = getSharedPreferences(LppHelper.STATION_FAVOURITES, MODE_PRIVATE).getBoolean(station.getRef_id(), false);

        setupUI();
        saveRecentSearchedStation();

    }

    private void setMapPaddingBottom(Float offset) {
        float maxMapPaddingBottom = (float) bottomSheetBehavior.getPeekHeight();

        if (mMap != null) {
            setPadding(0, 0, 0, Math.round(offset * maxMapPaddingBottom) + (int) maxMapPaddingBottom);
        }
    }

    private void saveRecentSearchedStation() {
        Api.addSavedSearchedItemsIds(station.getRef_id(), this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);

        // Setup Google maps UI
        setPadding(0,0,0, bottomSheetBehavior.getPeekHeight());

        mMap.setInfoWindowAdapter(new StationInfoWindow(this));
        Marker m = mMap.addMarker(new MarkerOptions().position(station.getLatLng()).icon(MapUtility.getMarkerIconFromDrawable(ContextCompat.getDrawable(this, R.drawable.station_circle))).anchor(0.5f, 0.5f));
        mMap.setOnCameraIdleListener(() -> lastValidMapCenter = mMap.getCameraPosition().target);
        m.setTag(station);
        m.showInfoWindow();
        mMap.setOnInfoWindowClickListener(marker -> bottomSheetBehavior.setState(ViewPagerBottomSheetBehavior.STATE_EXPANDED));

        // Focus on station
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(station.getLatLng(), 12.5f));

    }

    public void setupUI() {

        animation = new ElevationAnimation(16, header, mapFilter);

        // Set header
        name.setText(station.getName());
        center.setVisibility(station.isCenter() ? View.VISIBLE : View.GONE);

        // Favourite button toggle
        fav.setImageDrawable(getDrawable(favourite? R.drawable.ic_favorite_black_24dp : R.drawable.ic_favorite_border_black_24dp));
        fav.setOnClickListener(v1 -> {
            SharedPreferences sharedPreferences = getSharedPreferences(LppHelper.STATION_FAVOURITES, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(station.getRef_id(), !favourite);
            favourite = !favourite;
            fav.setImageDrawable(getResources().getDrawable(favourite? R.drawable.ic_favorite_black_24dp : R.drawable.ic_favorite_border_black_24dp));
            editor.apply();
        });

        // Set opposite station button listener
        oppositeBtn.setOnClickListener(view -> {
            oppositeBtn.setEnabled(false);

            int code;
            if (Integer.valueOf(station.getRef_id()) % 2 == 0)
                code = Integer.valueOf(station.getRef_id()) - 1 ;
            else code = Integer.valueOf(station.getRef_id()) + 1;

            // Query opposite station details
            Api.stationDetails(String.valueOf(code), true, (apiResponse, statusCode, success) -> runOnUiThread(() -> {
                if (success) {
                    // Start opposite route StationActivity and finish current
                    Intent intent = getIntent();
                    Station station = apiResponse.getData();
                    intent.putExtra("station", station);
                    finish();
                    startActivity(intent);
                } else {
                    // On error
                    CustomToast toast = new CustomToast(StationActivity.this);
                    if (statusCode == 500) {
                        toast
                            .setBackgroundColor(ContextCompat.getColor(StationActivity.this, R.color.colorAccent))
                            .setTextColor(Color.WHITE)
                            .setIconColor(Color.WHITE)
                            .setText(getString(R.string.opposite_error))
                            .setIcon(ContextCompat.getDrawable(StationActivity.this, R.drawable.ic_swap_vert_black_24dp))
                            .show(Toast.LENGTH_SHORT);
                    }
                    else toast.showDefault(statusCode);
                }
                oppositeBtn.setEnabled(true);
            }));
        });

        // Setup viewpager
        adapter = new Adapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        back.setOnClickListener(v -> super.onBackPressed());

    }

    @Override
    public void onHeaderChanged(boolean selected) {
        animation.elevate(selected);
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        else super.onBackPressed();
    }

    class Adapter extends FragmentPagerAdapter {

        public Adapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return LiveArrivalFragment.newInstance(station.getRef_id());
                case 1:
                    return RoutesOnStationFragment.newInstance(station.getRef_id(), station.getName());
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return getString(R.string.arrivals);
                case 1:
                    return getString(R.string.routes);
                default:
                    return "";
            }
        }

    }

}
