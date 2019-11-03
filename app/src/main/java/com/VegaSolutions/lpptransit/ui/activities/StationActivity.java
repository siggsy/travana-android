package com.VegaSolutions.lpptransit.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Station;
import com.VegaSolutions.lpptransit.ui.animations.ElevationAnimation;
import com.VegaSolutions.lpptransit.ui.fragments.FragmentHeaderCallback;
import com.VegaSolutions.lpptransit.ui.fragments.LiveArrivalFragment;
import com.VegaSolutions.lpptransit.ui.fragments.RoutesOnStationFragment;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

public class StationActivity extends AppCompatActivity implements FragmentHeaderCallback {


    public static final String STATION_CODE = "station_code";
    public static final String STATION_NAME = "station_name";
    public static final String STATION_CENTER = "station_center";

    // Views
    TextView name, center;
    FrameLayout header;
    ImageButton oppositeBtn;
    ImageView fav;
    ViewPager viewPager;
    TabLayout tabLayout;

    Adapter adapter;

    // Station data
    String station_code;
    String station_name;
    boolean station_center;
    boolean favourite;

    ElevationAnimation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station);


        // Assign all UI elements
        name = findViewById(R.id.station_title);
        header = findViewById(R.id.header);
        center = findViewById(R.id.station_center);
        oppositeBtn = findViewById(R.id.station_opposite_btn);
        fav = findViewById(R.id.station_favourite);
        viewPager = findViewById(R.id.station_pager);
        tabLayout = findViewById(R.id.station_tab_layout);

        // Get Intent data
        station_code = getIntent().getStringExtra(STATION_CODE);
        station_name = getIntent().getStringExtra(STATION_NAME);
        station_center = getIntent().getBooleanExtra(STATION_CENTER, false);
        favourite = getSharedPreferences("station_favourites", MODE_PRIVATE).getBoolean(station_code, false);

        setupUI();

    }



    public void setupUI() {

        animation = new ElevationAnimation(header, 16);

        // Set header
        name.setText(station_name);
        center.setVisibility(station_center ? View.VISIBLE : View.GONE);

        // Favourite button toggle
        fav.setImageDrawable(getDrawable(favourite? R.drawable.ic_favorite_black_24dp : R.drawable.ic_favorite_border_black_24dp));
        fav.setOnClickListener(v1 -> {
            SharedPreferences sharedPreferences = getSharedPreferences("station_favourites", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(station_code, !favourite);
            favourite = !favourite;
            fav.setImageDrawable(getResources().getDrawable(favourite? R.drawable.ic_favorite_black_24dp : R.drawable.ic_favorite_border_black_24dp));
            editor.apply();
        });

        // Set opposite station button listener
        oppositeBtn.setOnClickListener(view -> {
            oppositeBtn.setEnabled(false);

            int code;
            if (Integer.valueOf(station_code) % 2 == 0)
                code = Integer.valueOf(station_code) - 1 ;
            else code = Integer.valueOf(station_code) + 1;

            Api.stationDetails(code, true, (apiResponse, statusCode, success) -> {
                if (success) {
                    Intent intent = getIntent();
                    Station station = apiResponse.getData();
                    intent.putExtra("station_code", station.getRef_id());
                    intent.putExtra("station_name", station.getName());
                    intent.putExtra("station_center", Integer.valueOf(station.getRef_id()) % 2 != 0);
                    finish();
                    startActivity(intent);
                }
            });

        });

        adapter = new Adapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

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
                    return LiveArrivalFragment.newInstance(station_code);
                case 1:
                    return RoutesOnStationFragment.newInstance(station_code, station_name);
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
                    return "Prihodi";
                case 1:
                    return "Linije";
                default:
                    return "";
            }
        }

    }

}
