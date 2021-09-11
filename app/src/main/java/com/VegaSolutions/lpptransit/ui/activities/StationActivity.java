package com.VegaSolutions.lpptransit.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.TravanaApp;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Station;
import com.VegaSolutions.lpptransit.ui.animations.ElevationAnimation;
import com.VegaSolutions.lpptransit.ui.errorhandlers.CustomToast;
import com.VegaSolutions.lpptransit.ui.fragments.FragmentHeaderCallback;
import com.VegaSolutions.lpptransit.ui.fragments.lpp.LiveArrivalFragment;
import com.VegaSolutions.lpptransit.ui.fragments.lpp.RoutesOnStationFragment;
import com.VegaSolutions.lpptransit.utility.LppHelper;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.tabs.TabLayout;

public class StationActivity extends AppCompatActivity implements FragmentHeaderCallback {

    public static final String TAG = "StationActivity";
    public static final String STATION = "station";

    // Views
    TextView name, center;
    FrameLayout header;
    ImageButton oppositeBtn;
    ImageView fav, back;
    ViewPager viewPager;
    TabLayout tabLayout;

    int headerTopMargin = 0;

    View shadow;
    View root;
    View headerContainer;

    Adapter adapter;

    // Station data
    Station station;
    boolean favourite;

    ElevationAnimation animation;

    private TravanaApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_station);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        root = findViewById(R.id.root);

        app = TravanaApp.getInstance();

        setScreenSettings();
        initElements(root);

        // Get Intent data
        station = getIntent().getParcelableExtra(STATION);
        favourite = getSharedPreferences(LppHelper.STATION_FAVOURITES, MODE_PRIVATE).getBoolean(station.getRefId(), false);

        setupStationDetails();
        saveRecentSearchedStation();

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
            ViewGroup.MarginLayoutParams headerParams = (ViewGroup.MarginLayoutParams) headerContainer.getLayoutParams();
            headerParams.setMargins(0, headerTopMargin + insets.getSystemWindowInsetTop(), 0, 0);
            headerContainer.setLayoutParams(headerParams);
            return insets.consumeSystemWindowInsets();
        });
    }

    private void saveRecentSearchedStation() {
        Api.addSavedSearchedItemsIds(station.getRefId(), this);
    }

    private void initElements(View root) {

        // Assign all UI elements
        name = findViewById(R.id.station_title);
        header = findViewById(R.id.header);
        center = findViewById(R.id.station_center);
        oppositeBtn = findViewById(R.id.station_opposite_btn);
        fav = findViewById(R.id.station_favourite);
        viewPager = findViewById(R.id.station_pager);
        tabLayout = findViewById(R.id.station_tab_layout);
        back = findViewById(R.id.back);
        shadow = findViewById(R.id.shadow);
        headerContainer = findViewById(R.id.headerContainer);

        ViewGroup.MarginLayoutParams headerParams = (ViewGroup.MarginLayoutParams) headerContainer.getLayoutParams();
        headerTopMargin = headerParams.topMargin;

        // Set opposite station button listener
        oppositeBtn.setOnClickListener(view -> {
            oppositeBtn.setEnabled(false);

            int code;
            if (Integer.parseInt(station.getRefId()) % 2 == 0) {
                code = Integer.parseInt(station.getRefId()) - 1;
            } else {
                code = Integer.parseInt(station.getRefId()) + 1;
            }

            Station oppositeStation = getOppositeStation(code);
            if (oppositeStation != null) {
                // Start opposite route StationActivity and finish current
                Intent intent = getIntent();
                intent.putExtra("station", oppositeStation);
                finish();
                startActivity(intent);
            } else {
                CustomToast toast = new CustomToast(StationActivity.this);
                toast.setBackgroundColor(ContextCompat.getColor(StationActivity.this, R.color.colorAccent))
                        .setTextColor(Color.WHITE)
                        .setIconColor(Color.WHITE)
                        .setText(getString(R.string.opposite_error))
                        .setIcon(ContextCompat.getDrawable(StationActivity.this, R.drawable.ic_swap))
                        .show(Toast.LENGTH_SHORT);

                // enable button when current toast disappear
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> {
                    oppositeBtn.setEnabled(true);
                }, 2000);
            }
        });

        // Setup viewpager
        adapter = new Adapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        back.setOnClickListener(v -> super.onBackPressed());
    }

    private void setupStationDetails() {

        animation = new ElevationAnimation(16, header);

        // Set header
        name.setText(station.getName());
        center.setVisibility(station.isCenter() ? View.VISIBLE : View.GONE);

        // Favourite button toggle
        fav.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), favourite ? R.drawable.ic_heart_fill : R.drawable.ic_heart_border));
        fav.setOnClickListener(v1 -> {
            SharedPreferences sharedPreferences = getSharedPreferences(LppHelper.STATION_FAVOURITES, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(station.getRefId(), !favourite);
            favourite = !favourite;
            fav.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), favourite ? R.drawable.ic_heart_fill : R.drawable.ic_heart_border));
            editor.apply();
        });

    }

    private Station getOppositeStation(int stationCode) {
        if (!app.areStationsLoaded()) {
            return null;
        }

        for (Station station : app.getStations()) {
            if (station.getRefId().equals(stationCode + "")) {
                return station;
            }
        }
        return null;
    }

    @Override
    public void onHeaderChanged(boolean selected) {
        animation.elevate(selected);
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
                    return LiveArrivalFragment.newInstance(station.getRefId());
                case 1:
                    return RoutesOnStationFragment.newInstance(station.getRefId(), station.getName());
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
