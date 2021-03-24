package com.VegaSolutions.lpptransit.ui.activities.lpp;

import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.TimetableWrapper;
import com.VegaSolutions.lpptransit.ui.animations.ElevationAnimation;
import com.VegaSolutions.lpptransit.ui.errorhandlers.CustomToast;
import com.VegaSolutions.lpptransit.utility.Colors;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;
import com.google.android.flexbox.FlexboxLayout;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DepartureActivity extends AppCompatActivity {

    public static final String STATION_CODE = "station_code";
    public static final String STATION_NAME = "station_name";
    public static final String ROUTE_NUMBER = "route_number";
    public static final String ROUTE_NAME = "route_name";
    public static final String ROUTE_GARAGE = "route_garage";

    // Activity parameters
    private String station_code;
    private String station_name;
    private String route_number;
    private String route_name;
    private boolean route_garage;

    // Activity UI elements
    @BindView(R.id.departure_route_name) TextView routeName;
    @BindView(R.id.route_station_number) TextView routeNumber;
    @BindView(R.id.departure_station_name) TextView stationName;
    @BindView(R.id.station_center) TextView stationCenter;
    @BindView(R.id.include) View routeNumberCircle;
    @BindView(R.id.departure_rv) RecyclerView rv;
    @BindView(R.id.header) FrameLayout header;
    @BindView(R.id.departure_no_departures_error) View depErr;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.back) View back;

    private Adapter adapter;

    // Private local variables
    private int textColor;
    private int backGroundColor;

    private ElevationAnimation elevationAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ViewGroupUtils.isDarkTheme(this) ? R.style.DarkTheme : R.style.WhiteTheme);
        setContentView(R.layout.activity_departure);
        ButterKnife.bind(this);

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
            ViewGroup.MarginLayoutParams backParams = (ViewGroup.MarginLayoutParams) back.getLayoutParams();
            backParams.setMargins(0, insets.getSystemWindowInsetTop(), 0, 0);
            return insets.consumeSystemWindowInsets();
        });

        // Get theme default colors
        int[] attribute = new int[] { android.R.attr.textColor, R.attr.backgroundViewColor };
        TypedArray array = obtainStyledAttributes(ViewGroupUtils.isDarkTheme(this) ? R.style.DarkTheme : R.style.WhiteTheme, attribute);
        textColor = array.getColor(0, Color.BLACK);
        backGroundColor = array.getColor(1, Color.WHITE);
        array.recycle();

        // Get activity parameters
        station_code = getIntent().getStringExtra(STATION_CODE);
        station_name = getIntent().getStringExtra(STATION_NAME);
        route_number = getIntent().getStringExtra(ROUTE_NUMBER);
        route_name = getIntent().getStringExtra(ROUTE_NAME);
        route_garage = getIntent().getBooleanExtra(ROUTE_GARAGE, false);

        // Set UI elements
        if (route_garage)
            route_name += " (" + getString(R.string.garage).toUpperCase() + ")";
        routeName.setText(route_name );
        routeNumber.setText(route_number);
        routeNumber.setTextSize(16f);
        stationName.setText(station_name);
        stationCenter.setVisibility(Integer.parseInt(station_code) % 2 != 0 ? View.VISIBLE : View.GONE);
        routeNumberCircle.getBackground().setTint(Colors.getColorFromString(route_number));

        back.setOnClickListener(v -> super.onBackPressed());

        adapter = new Adapter();

        elevationAnimation = new ElevationAnimation(16, header);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                elevationAnimation.elevate(rv.canScrollVertically(-1));
            }
        });


        // Get group number
        String group = route_number.replaceAll("[^0-9]", "");

        // Calculate for request
        DateTime now = DateTime.now();
        int next = 28 - now.getHourOfDay();
        int prev = now.getHourOfDay() - 6;

        Api.timetable(station_code, 8, 8, (apiResponse, statusCode, success) -> {
            runOnUiThread(() -> {

                // Remove progress bar
                progressBar.setVisibility(View.GONE);
                if (success) {
                    TimetableWrapper timetableWrapper = apiResponse.getData();

                    // Notify user if timetable is empty
                    if (timetableWrapper.getRoute_groups().get(0).getRoutes().isEmpty())
                        depErr.setVisibility(View.VISIBLE);

                    // Search for the right timetable
                    for (TimetableWrapper.RouteGroup.Route route : timetableWrapper.getRoute_groups().get(0).getRoutes()) {
                        if (route.getParent_name().equals(route_name)) {
                            adapter.setTimetables(route.getTimetable());
                            // Notify user if empty
                            if (adapter.timetables.isEmpty())
                                depErr.setVisibility(View.VISIBLE);
                            return;
                        }
                    }

                    // If the right timetable was not found
                    depErr.setVisibility(View.VISIBLE);
                }

                // In case of error, show Toast with error message
                else new CustomToast(this).showDefault(statusCode);

            });
        }, Integer.parseInt(group));

    }

    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        List<TimetableWrapper.RouteGroup.Route.Timetable> timetables = new ArrayList<>();

        private void setTimetables(List<TimetableWrapper.RouteGroup.Route.Timetable> timetables) {
            this.timetables = timetables;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(getLayoutInflater().inflate(R.layout.template_departure_hour, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            TimetableWrapper.RouteGroup.Route.Timetable timetable = timetables.get(position);

            // Set hour number
            holder.hour.setText(String.valueOf(timetable.getHour()));
            holder.minutes.removeAllViews();

            // Set minutes in an hour
            for (int min : timetable.getMinutes()) {
                TextView textView = (TextView) getLayoutInflater().inflate(R.layout.template_departure_min, holder.minutes, false);

                textView.setText(String.format(Locale.getDefault(), "%d:%02d", timetable.getHour(), min));
                holder.minutes.addView(textView);
            }

            if (timetable.isCurrent()) {
                holder.container.getBackground().setTint(ColorUtils.blendARGB(backGroundColor, ViewGroupUtils.isDarkTheme(getApplication()) ? Color.WHITE : Color.GRAY, 0.1f));
            } else {
                holder.hour.setTextColor(textColor);
                holder.container.getBackground().setTint(backGroundColor);
            }

        }

        @Override
        public int getItemCount() {
            return timetables.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            TextView hour;
            FlexboxLayout minutes;
            CardView container;

            private ViewHolder(@NonNull View itemView) {
                super(itemView);

                hour = itemView.findViewById(R.id.departure_hour_hour);
                minutes = itemView.findViewById(R.id.departure_hour_minutes);
                container = itemView.findViewById(R.id.departure_root);

            }
        }

    }

}
