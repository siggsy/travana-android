package com.VegaSolutions.lpptransit.ui.activities;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.TravanaApp;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.TimetableWrapper;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.TimetableWrapper.RouteGroup.Route;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.TimetableWrapper.RouteGroup.Route.Timetable;
import com.VegaSolutions.lpptransit.ui.animations.ElevationAnimation;
import com.VegaSolutions.lpptransit.utility.Colors;
import com.VegaSolutions.lpptransit.utility.NetworkConnectivityManager;
import com.VegaSolutions.lpptransit.utility.ScreenState;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;
import com.google.android.flexbox.FlexboxLayout;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.VegaSolutions.lpptransit.utility.ScreenState.DONE;
import static com.VegaSolutions.lpptransit.utility.ScreenState.ERROR;
import static com.VegaSolutions.lpptransit.utility.ScreenState.LOADING;

public class DepartureActivity extends AppCompatActivity {

    public static final String TAG = DepartureActivity.class.getSimpleName();

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
    private LocalDateTime now;

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
    @ColorInt int highlightColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_departure);

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.departureActive, typedValue, true);
        highlightColor = typedValue.data;

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

        now = LocalDateTime.now();
        titleText.setText(getString(R.string.departures));

        retrieveDepartures();
    }

    @SuppressLint("WrongConstant")
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
            Insets systemInsets = insets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams headerParams = (ViewGroup.MarginLayoutParams) back.getLayoutParams();
            headerParams.setMargins(0, backTopMargin + systemInsets.top, 0, 0);
            back.setLayoutParams(headerParams);
            return WindowInsetsCompat.CONSUMED;
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
        int currentDay = now.getDayOfYear();
        int next = 48;
        int prev = now.getHour();

        Api.timetable(stationCode, next, prev, (apiResponse, statusCode, success) -> {
            if (success) {
                TimetableWrapper timetableWrapper = apiResponse.getData();

                // Search for the right timetable
                ArrayList<Timetable> normal = new ArrayList<>();
                ArrayList<Timetable> garage = new ArrayList<>();
                for (Route route : timetableWrapper.getRouteGroups().get(0).getRoutes()) {
                    if (route.getParentName().equals(routeName) && route.isGarage() == routeGarage) {
                        if (route.getTimetable().isEmpty()) {
                            setupUi(DONE);
                            return;
                        }
                        DateTimeFormatter dtf= DateTimeFormatter.ISO_DATE_TIME;
                        ArrayList<Adapter.Item> items = new ArrayList<>();
                        int day = -1;
                        for (Timetable timetable : route.getTimetable()) {
                            LocalDate date = LocalDate.parse(timetable.getTimestamp(), dtf);
                            if (day != date.getDayOfYear()) {
                                items.add(new Adapter.HeaderItem(date));
                                day = date.getDayOfYear();
                            }
                            int hour = timetable.getHour() == 24 ? 0 : timetable.getHour();
                            boolean isCurrent = date.getDayOfYear() == now.getDayOfYear() && hour == now.getHour();
                            items.add(new Adapter.DepartureItem(isCurrent, hour, timetable.getMinutes()));
                        }
                        runOnUiThread(() -> {
                            setupUi(DONE);
                            adapter.setItems(items);
                            // Notify user if empty
                            if (items.isEmpty()) {
                                llNoDepartures.setVisibility(View.VISIBLE);
                            } else {
                                llNoDepartures.setVisibility(View.GONE);
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

        adapter = new Adapter(getLayoutInflater(), highlightColor, backGroundColor);
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

    private static class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_HEADER = 0;
        private static final int TYPE_NORMAL = 1;

        List<Item> items = new ArrayList<>();

        static abstract class Item {
            public final boolean isHeader;
            Item(boolean isHeader) {
                this.isHeader = isHeader;
            }
        }

        public static class HeaderItem extends Item {
            public final LocalDate date;
            public HeaderItem(LocalDate date) {
                super(true);
                this.date = date;
            }
        }

        public static class DepartureItem extends Item {
            public final boolean isCurrent;
            public final int hour;
            public final int[] minutes;
            public DepartureItem(boolean isCurrent, int hour, int[] minutes) {
                super(false);
                this.isCurrent = isCurrent;
                this.hour = hour;
                this.minutes = minutes;
            }
        }

        private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEEE d. M.", Locale.getDefault());
        private final LayoutInflater inflater;
        private final int highlightColor;
        private final int backgroundColor;
        public Adapter(LayoutInflater inflater, int highlightColor, int backgroundColor) {
            this.inflater = inflater;
            this.highlightColor = highlightColor;
            this.backgroundColor = backgroundColor;
        }

        private void setItems(List<Item> items) {
            this.items = items;
            notifyDataSetChanged(); // Only called once, so we don't really care
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType) {
                case TYPE_HEADER:
                    return new HeaderHolder(inflater.inflate(R.layout.template_departure_header, parent, false));
                case TYPE_NORMAL:
                    return new DepartureHolder(inflater.inflate(R.layout.template_departure_hour, parent, false));
                default:
                    throw new IllegalArgumentException("Invalid RecyclerView Item type '" + viewType + "'");
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Item item = items.get(position);
            if (item.isHeader) {
                HeaderItem header = (HeaderItem) item;
                HeaderHolder vh = (HeaderHolder) holder;
                vh.date.setText(header.date.format(dtf));
            } else {
                DepartureItem departure = (DepartureItem) item;
                DepartureHolder vh = (DepartureHolder) holder;

                // Set time
                vh.hour.setText(String.valueOf(departure.hour));
                vh.minutes.removeAllViews();
                for (int min : departure.minutes) {
                    TextView textView = (TextView) inflater.inflate(R.layout.template_departure_min, vh.minutes, false);
                    textView.setText(String.format(Locale.getDefault(), "%d:%02d", departure.hour, min));
                    vh.minutes.addView(textView);
                }

                // Highlight current
                if (departure.isCurrent) {
                    vh.container.setBackgroundColor(highlightColor);
                } else {
                    vh.container.setBackgroundColor(0);
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (items.get(position).isHeader) {
                return TYPE_HEADER;
            } else {
                return TYPE_NORMAL;
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        private static class DepartureHolder extends RecyclerView.ViewHolder {
            TextView hour;
            FlexboxLayout minutes;
            LinearLayout container;

            private DepartureHolder(@NonNull View itemView) {
                super(itemView);
                hour = itemView.findViewById(R.id.departure_hour_hour);
                minutes = itemView.findViewById(R.id.departure_hour_minutes);
                container = itemView.findViewById(R.id.departure_root);
            }
        }

        private static class HeaderHolder extends RecyclerView.ViewHolder {
            TextView date;
            private HeaderHolder(@NonNull View itemView) {
                super(itemView);
                date = itemView.findViewById(R.id.tv_date);
            }
        }

    }

}
