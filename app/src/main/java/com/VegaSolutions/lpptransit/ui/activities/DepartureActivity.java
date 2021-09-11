package com.VegaSolutions.lpptransit.ui.activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.TravanaApp;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.TimetableWrapper;
import com.VegaSolutions.lpptransit.ui.animations.ElevationAnimation;
import com.VegaSolutions.lpptransit.utility.Colors;
import com.VegaSolutions.lpptransit.utility.NetworkConnectivityManager;
import com.VegaSolutions.lpptransit.utility.ScreenState;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;
import com.google.android.flexbox.FlexboxLayout;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.VegaSolutions.lpptransit.utility.ScreenState.DONE;
import static com.VegaSolutions.lpptransit.utility.ScreenState.ERROR;
import static com.VegaSolutions.lpptransit.utility.ScreenState.LOADING;

public class DepartureActivity extends AppCompatActivity {

    public static final String STATION_CODE = "station_code";
    public static final String STATION_NAME = "station_name";
    public static final String ROUTE_NUMBER = "route_number";
    public static final String ROUTE_NAME = "route_name";
    public static final String ROUTE_GARAGE = "route_garage";

    // Activity parameters
    private String stationCode;
    private String stationName;
    private String routeNumber;
    private String routeName;
    private boolean routeGarage;

    private Adapter adapter;
    private ElevationAnimation elevationAnimation;

    // Private local variables
    private int backGroundColor;

    private TravanaApp app;
    private NetworkConnectivityManager networkConnectivityManager;
    private DateTime now;

    // Activity UI elements
    TextView routeNameText;
    TextView routeNumberText;
    TextView stationNameText;
    TextView stationCenterText;
    View routeNumberCircleView;
    TextView titleText;
    RecyclerView rv;
    FrameLayout header;
    LinearLayout llNoDepartures;
    ProgressBar progressBar;
    View back;
    LinearLayout errorContainer;
    TextView errorText;
    ImageView errorImageView;
    TextView tryAgainText;

    int backTopMargin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_departure);
        initElements();

        app = TravanaApp.getInstance();
        networkConnectivityManager = app.getNetworkConnectivityManager();

        setScreenSettings();

        // Get activity parameters
        stationCode = getIntent().getStringExtra(STATION_CODE);
        stationName = getIntent().getStringExtra(STATION_NAME);
        routeNumber = getIntent().getStringExtra(ROUTE_NUMBER);
        routeName = getIntent().getStringExtra(ROUTE_NAME);
        routeGarage = getIntent().getBooleanExtra(ROUTE_GARAGE, false);

        // Set UI elements
        if (routeGarage)
            routeName += " (" + getString(R.string.garage).toUpperCase() + ")";
        routeNameText.setText(routeName);
        routeNumberText.setText(routeNumber);
        stationNameText.setText(stationName);
        stationCenterText.setVisibility(Integer.parseInt(stationCode) % 2 != 0 ? View.VISIBLE : View.GONE);
        routeNumberCircleView.getBackground().setTint(Colors.getColorFromString(routeNumber));

        now = DateTime.now();
        SimpleDateFormat sdf = new SimpleDateFormat("d. M", Locale.getDefault());
        titleText.setText(getString(R.string.departures, sdf.format(now.toDate())));

        retrieveDepartures();
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
            ViewGroup.MarginLayoutParams headerParams = (ViewGroup.MarginLayoutParams) back.getLayoutParams();
            headerParams.setMargins(0, backTopMargin + insets.getSystemWindowInsetTop(), 0, 0);
            back.setLayoutParams(headerParams);
            return insets.consumeSystemWindowInsets();
        });
    }

    private void retrieveDepartures() {

        if (!networkConnectivityManager.isConnectionAvailable()) {
            setupUi(ERROR);
            setErrorUi(this.getResources().getString(R.string.no_internet_connection), R.drawable.ic_no_wifi);
            return;
        }
        setupUi(LOADING);

        // Get group number
        String group = routeNumber.replaceAll("[^0-9]", "");

        // Calculate for request
        int next = 25 - now.getHourOfDay();
        int prev = now.getHourOfDay();

        Api.timetable(stationCode, next, prev, (apiResponse, statusCode, success) -> {
            if (success) {
                setupUi(DONE);

                TimetableWrapper timetableWrapper = apiResponse.getData();
                // Search for the right timetable
                for (TimetableWrapper.RouteGroup.Route route : timetableWrapper.getRouteGroups().get(0).getRoutes()) {
                    if (route.getParentName().equals(routeName)) {
                        runOnUiThread(() -> {
                            adapter.setTimetables(route.getTimetable());
                            // Notify user if empty
                            if (adapter.timetables.isEmpty()) {
                                llNoDepartures.setVisibility(View.VISIBLE);
                            }
                        });
                        return;
                    }
                }
            }

            // In case of error, show error message
            else {
                setupUi(ERROR);
                setErrorUi(this.getResources().getString(R.string.error_loading), R.drawable.ic_error_outline);
            }
        }, Integer.parseInt(group));
    }

    void setErrorUi(String errorName, int errorIconCode) {
        runOnUiThread(() -> {
            errorText.setText(errorName);
            errorImageView.setImageResource(errorIconCode);
        });
    }

    void setupUi(ScreenState screenState) {
        runOnUiThread(() -> {
            switch (screenState) {
                case DONE: {
                    this.progressBar.setVisibility(View.GONE);
                    this.rv.setVisibility(View.VISIBLE);
                    this.errorContainer.setVisibility(View.GONE);
                    break;
                }
                case LOADING: {
                    this.progressBar.setVisibility(View.VISIBLE);
                    this.rv.setVisibility(View.GONE);
                    this.errorContainer.setVisibility(View.GONE);
                    break;
                }
                case ERROR: {
                    this.progressBar.setVisibility(View.GONE);
                    this.rv.setVisibility(View.GONE);
                    this.errorContainer.setVisibility(View.VISIBLE);
                    break;
                }
            }
        });
    }

    void initElements() {
        routeNameText = findViewById(R.id.departure_route_name);
        routeNumberText = findViewById(R.id.route_station_number);
        stationNameText = findViewById(R.id.departure_station_name);
        stationCenterText = findViewById(R.id.station_center);
        routeNumberCircleView = findViewById(R.id.route_station_circle);
        titleText = findViewById(R.id.timetable_title_tv);
        rv = findViewById(R.id.departure_rv);
        header = findViewById(R.id.header);
        llNoDepartures = findViewById(R.id.no_departures_container);
        back = findViewById(R.id.back);
        progressBar = findViewById(R.id.progress_bar);
        errorText = findViewById(R.id.tv_error);
        errorImageView = findViewById(R.id.iv_error);
        tryAgainText = findViewById(R.id.tv_try_again);
        errorContainer = findViewById(R.id.ll_error_container);

        back.setOnClickListener(v -> super.onBackPressed());
        ViewGroup.MarginLayoutParams backParams = (ViewGroup.MarginLayoutParams) back.getLayoutParams();
        backTopMargin = backParams.topMargin;

        tryAgainText.setOnClickListener(v -> {
            retrieveDepartures();
        });

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
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        List<TimetableWrapper.RouteGroup.Route.Timetable> timetables = new ArrayList<>();

        private void setTimetables(List<TimetableWrapper.RouteGroup.Route.Timetable> timetables) {
            this.timetables = timetables;
            notifyDataSetChanged();

            if (timetables.size() == 0) {
                llNoDepartures.setVisibility(View.VISIBLE);
            } else {
                llNoDepartures.setVisibility(View.GONE);
            }
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
                holder.container.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.departure_active));
            } else {
                holder.container.setBackgroundColor(backGroundColor);
            }

        }

        @Override
        public int getItemCount() {
            return timetables.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            TextView hour;
            FlexboxLayout minutes;
            RelativeLayout container;

            private ViewHolder(@NonNull View itemView) {
                super(itemView);

                hour = itemView.findViewById(R.id.departure_hour_hour);
                minutes = itemView.findViewById(R.id.departure_hour_minutes);
                container = itemView.findViewById(R.id.departure_root);

            }
        }

    }

}
