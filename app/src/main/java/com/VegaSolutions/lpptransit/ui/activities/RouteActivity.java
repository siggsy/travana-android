package com.VegaSolutions.lpptransit.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.BlendModeColorFilterCompat;
import androidx.core.graphics.BlendModeCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.TravanaApp;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.ArrivalOnRoute;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.BusOnRoute;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Route;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.StationOnRoute;
import com.VegaSolutions.lpptransit.ui.animations.ElevationAnimation;
import com.VegaSolutions.lpptransit.ui.custommaps.BusMarkerManager;
import com.VegaSolutions.lpptransit.ui.errorhandlers.CustomToast;
import com.VegaSolutions.lpptransit.ui.errorhandlers.TopMessage;
import com.VegaSolutions.lpptransit.utility.Colors;
import com.VegaSolutions.lpptransit.utility.MapUtility;
import com.VegaSolutions.lpptransit.utility.NetworkConnectivityManager;
import com.VegaSolutions.lpptransit.utility.ScreenState;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonLineStringStyle;
import com.google.maps.android.ui.IconGenerator;

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import biz.laenger.android.vpbs.ViewPagerBottomSheetBehavior;

public class RouteActivity extends MapFragmentActivity {

    public static final String TAG = "RouteActivity";
    private static final int MAX_FAILED_CALLS_IN_ROW = 2;

    public static final String ROUTE_NAME = "route_name";
    public static final String ROUTE_NUMBER = "route_number";
    public static final String ROUTE_ID = "route_id";
    public static final String TRIP_ID = "trip_id";
    public static final String STATION_ID = "station_id";

    // Activity parameters
    private String routeName;
    private String routeNumber;
    private String routeId;
    private String tripId;
    private String stationId;

    // Activity UI elements
    ImageView backBtn;
    TextView number;
    TextView name;
    View circle;
    TopMessage routeLoading;
    RecyclerView recyclerView;
    View header;
    View bottomSheet;
    ImageButton opposite;
    View shadow;
    ProgressBar progressBar;
    LinearLayout errorContainer;
    TextView errorText;
    ImageView errorImageView;
    TextView tryAgainText;

    View bottom;
    View mapFilter;

    private ElevationAnimation elevationAnimation;
    private Adapter adapter;
    private ViewPagerBottomSheetBehavior behavior;

    // Google maps parameters
    private final int UPDATE_TIME = 10000;
    private Handler handler;
    private BusMarkerManager busManager;
    private MarkerOptions busOptions;
    private MarkerOptions stationOptions;
    LatLng lastValidMapCenter = ljubljana;

    private boolean hour;
    private int backColor;
    private int color;

    private boolean isRouteDrawn = false;
    private boolean isFirstCallingUpdateBuses = true;
    private int failedCallingUpdateBusesInRow = 0;
    private boolean isFirstCallingUpdateArrivals = true;
    private int failedCallingUpdateArrivalsInRow = 0;

    private TravanaApp app;
    private NetworkConnectivityManager networkConnectivityManager;

    private final Runnable arrivalsUpdaterRunnable = new Runnable() {
        @Override
        public void run() {
            updateArrivals();
            handler.postDelayed(this, UPDATE_TIME);
        }
    };

    private final Runnable busesUpdaterRunnable = new Runnable() {
        @Override
        public void run() {
            updateBuses();
            handler.postDelayed(this, UPDATE_TIME);
        }
    };

    private void startArrivalsUpdater() {
        handler.postDelayed(arrivalsUpdaterRunnable, UPDATE_TIME);
    }

    private void stopArrivalsUpdater() {
        handler.removeCallbacks(arrivalsUpdaterRunnable);
    }

    private void startBusesUpdater() {
        handler.postDelayed(busesUpdaterRunnable, UPDATE_TIME);
    }

    private void stopBusesUpdater() {
        handler.removeCallbacks(busesUpdaterRunnable);
    }

    private void updateBuses() {
        if (!networkConnectivityManager.isConnectionAvailable()) {
            if (isFirstCallingUpdateBuses || failedCallingUpdateBusesInRow >= MAX_FAILED_CALLS_IN_ROW) {
                setupUi(ScreenState.ERROR);
                busManager.removeAllBuses();
                setErrorUi(this.getResources().getString(R.string.no_internet_connection), R.drawable.ic_no_wifi, NetworkConnectivityManager.NO_INTERNET_CONNECTION);
            }
            isFirstCallingUpdateBuses = false;
            failedCallingUpdateBusesInRow++;
            return;
        }
        if (isFirstCallingUpdateBuses || failedCallingUpdateBusesInRow >= MAX_FAILED_CALLS_IN_ROW) {
            setupUi(ScreenState.LOADING);
        }
        Api.busesOnRoute(routeNumber, (apiResponse, statusCode, success) -> {

            if (success) {
                failedCallingUpdateBusesInRow = 0;
                List<BusOnRoute> buses = new ArrayList<>();
                // Filter by trip ID.
                for (BusOnRoute busOnRoute : apiResponse.getData()) {
                    if (busOnRoute.getTripId().equals(tripId)) {
                        buses.add(busOnRoute);
                    }
                }
                runOnUiThread(() -> {
                    Log.e(TAG, buses + "");

                    // Update markers and queue another update.
                    busManager.update(buses);
                });
                setupUi(ScreenState.DONE);
            } else {
                if (isFirstCallingUpdateBuses || failedCallingUpdateBusesInRow >= MAX_FAILED_CALLS_IN_ROW) {
                    runOnUiThread(() -> {
                        // Remove update queue and show error message
                        busManager.removeAllBuses();
                        setupUi(ScreenState.ERROR);
                        setErrorUi(this.getResources().getString(R.string.error_loading), R.drawable.ic_error_outline, NetworkConnectivityManager.ERROR_DURING_LOADING);
                    });
                }
                failedCallingUpdateBusesInRow++;
            }
        });
        isFirstCallingUpdateBuses = false;
    }

    private void updateArrivals() {

        if (!networkConnectivityManager.isConnectionAvailable()) {
            if (isFirstCallingUpdateArrivals || failedCallingUpdateArrivalsInRow >= MAX_FAILED_CALLS_IN_ROW) {
                setupUi(ScreenState.ERROR);
                setErrorUi(this.getResources().getString(R.string.no_internet_connection), R.drawable.ic_no_wifi, NetworkConnectivityManager.NO_INTERNET_CONNECTION);
            }
            isFirstCallingUpdateArrivals = false;
            failedCallingUpdateArrivalsInRow++;
            return;
        }
        if (isFirstCallingUpdateArrivals || failedCallingUpdateArrivalsInRow >= MAX_FAILED_CALLS_IN_ROW) {
            setupUi(ScreenState.LOADING);
        }
        Api.arrivalsOnRoute(tripId, (apiResponse, statusCode, success) -> {

            if (success) {
                failedCallingUpdateArrivalsInRow = 0;

                List<ArrivalOnRoute> stationsOnRoute = apiResponse.getData();

                // Sort stations.
                Collections.sort(stationsOnRoute, (o1, o2) -> Integer.compare(o1.getOrderNo(), o2.getOrderNo()));
                for (ArrivalOnRoute station : stationsOnRoute) {
                    Collections.sort(station.getArrivals(), (o1, o2) -> {
                        if (o1.getType() == 2) return -1;
                        else if (o2.getType() == 2) return 1;
                        else return Integer.compare(o1.getEtaMin(), o2.getEtaMin());
                    });
                }
                adapter.setStationsOnRoute(stationsOnRoute);
                RouteActivity.this.runOnUiThread(() -> adapter.notifyDataSetChanged());

                // Draw stations - called just once
                if (!isRouteDrawn) {

                    List<LatLng> latLngs = new ArrayList<>();
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                    // Add station markers.
                    for (StationOnRoute stationOnRoute : stationsOnRoute) {
                        LatLng latLng = stationOnRoute.getLatLng();
                        latLngs.add(latLng);
                        builder.include(latLng);
                        RouteActivity.this.runOnUiThread(() -> {
                            Marker marker = mMap.addMarker(stationOptions.position(latLng).title(stationOnRoute.getName()));
                            marker.setTag(String.valueOf(stationOnRoute.getCode_id()));
                            if (Integer.parseInt(stationId) == stationOnRoute.getCode_id()) {
                                marker.showInfoWindow();
                            }
                        });
                    }

                    // Connect stations with polyline and move the camera.
                    Api.routes(routeId, true, (routeShapeResponse, shapeStatusCode, shapeSuccess) -> {
                        runOnUiThread(() -> {
                            if (shapeSuccess && routeShapeResponse != null) {
                                Route route = null;
                                for (Route r : routeShapeResponse.getData()) {
                                    if (r.getTripId().equals(tripId)) {
                                        route = r;
                                    }
                                }
                                if (route != null) {
                                    Log.e(TAG, route.getGeoJSON().toString());
                                    GeoJsonLayer layer = new GeoJsonLayer(mMap, route.getGeoJSON());
                                    GeoJsonLineStringStyle style = new GeoJsonLineStringStyle();
                                    style.setColor(Colors.getColorFromString(routeNumber));
                                    for (GeoJsonFeature feature : layer.getFeatures()) {
                                        feature.setLineStringStyle(style);
                                    }
                                    layer.addLayerToMap();
                                    LatLngBounds bounds = builder.build();
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
                                }
                            } else if (!stationsOnRoute.isEmpty()) {
                                LatLngBounds bounds = builder.build();
                                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
                                mMap.addPolyline(new PolylineOptions().addAll(latLngs).width(14f).color(Colors.getColorFromString(routeNumber)));
                            } else {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ljubljana, 11.5f));
                            }

                            isRouteDrawn = true;
                        });
                    });
                }
                setupUi(ScreenState.DONE);
            } else {
                if (isFirstCallingUpdateArrivals || failedCallingUpdateArrivalsInRow >= MAX_FAILED_CALLS_IN_ROW) {
                    setupUi(ScreenState.ERROR);
                    setErrorUi(this.getResources().getString(R.string.error_loading), R.drawable.ic_error_outline, NetworkConnectivityManager.ERROR_DURING_LOADING);
                }
                failedCallingUpdateArrivalsInRow++;
            }
        });
        isFirstCallingUpdateArrivals = false;
    }

    private void setElements() {

        setScreenSettings();

        locationIcon = findViewById(R.id.maps_location_icon);

        toHide.add(bottomSheet);
        toHide.add(shadow);

        behavior = ViewPagerBottomSheetBehavior.from(bottomSheet);

        mapFilter = findViewById(R.id.map_filter);
        behavior.setBottomSheetCallback(new ViewPagerBottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                switch (behavior.getState()) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                    case BottomSheetBehavior.STATE_SETTLING:
                        setMapPaddingBottom(slideOffset);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(lastValidMapCenter));
                        mapFilter.setAlpha(slideOffset);
                        break;
                }
            }
        });

        int[] attribute = new int[]{android.R.attr.textColor, R.attr.backgroundViewColor};
        TypedArray array = obtainStyledAttributes(ViewGroupUtils.isDarkTheme(this) ? R.style.DarkTheme : R.style.WhiteTheme, attribute);
        backColor = array.getColor(1, Color.WHITE);
        color = ViewGroupUtils.isDarkTheme(this) ? Color.WHITE : Color.BLACK;
        array.recycle();

        // Header elevation animation
        elevationAnimation = new ElevationAnimation(16, header, mapFilter);

        // Get drawable resource for markers.
        int color = Colors.getColorFromString(routeNumber);

        // Set bus and station marker style
        View v = getLayoutInflater().inflate(R.layout.station_node_maps, null);

        v.findViewById(R.id.stroke).getBackground().setColorFilter(BlendModeColorFilterCompat.createBlendModeColorFilterCompat(color, BlendModeCompat.SRC_IN));
        v.findViewById(R.id.solid).getBackground().setColorFilter(
                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                        ContextCompat.getColor(this, ViewGroupUtils.isDarkTheme(this) ? R.color.color_main_background_dark : R.color.color_main_background), BlendModeCompat.SRC_IN));
        IconGenerator generator = new IconGenerator(this);
        generator.setBackground(null);
        generator.setContentView(v);
        busOptions = new MarkerOptions().anchor(0.5f, 0.5f).zIndex(1f).icon(MapUtility.getMarkerIconFromDrawable(
                Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.ic_water_drop)), 80, 80)).flat(true);
        stationOptions = new MarkerOptions().anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromBitmap(generator.makeIcon())).flat(true);

        // Setup views.
        backBtn.setOnClickListener(vi -> super.onBackPressed());
        number.setText(routeNumber);
        number.setTextSize(14f);
        circle.getBackground().setTint(Colors.getColorFromString(routeNumber));
        routeLoading.showLoading(true);
        routeLoading.setErrorMsgBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
        routeLoading.setErrorMsgColor(Color.WHITE);
        routeLoading.setErrorIconColor(Color.WHITE);
        routeLoading.setRefreshClickEvent(vi -> {
            updateBuses();
            updateArrivals();
        });
        tryAgainText.setOnClickListener(view -> {
            updateBuses();
            updateArrivals();
        });

        name.setText(routeName);
        name.setSelected(true);

        adapter = new Adapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                elevationAnimation.elevate(recyclerView.canScrollVertically(-1));
            }
        });

        opposite.setOnClickListener(vi -> Api.routes(routeId, (apiResponse, statusCode, success) -> {
            if (success) {
                List<Route> routes = apiResponse.getData();

                // Show list of all trips
                if (routes.size() > 2) {
                    String[] trips = new String[routes.size()];
                    for (int i = 0, routesSize = routes.size(); i < routesSize; i++) {
                        Route route = routes.get(i);
                        trips[i] = route.getRouteName();
                    }

                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this,
                                ViewGroupUtils.isDarkTheme(getApplication())
                                        ? R.style.DarkAlert : R.style.WhiteAlert);

                        builder.setTitle(getString(R.string.select_route));
                        builder.setItems(trips, (dialog, which) -> runOnUiThread(() -> {
                            Route route = routes.get(which);
                            Intent i = new Intent(this, RouteActivity.class);
                            i.putExtra(RouteActivity.ROUTE_NAME, route.getRouteName());
                            i.putExtra(RouteActivity.ROUTE_NUMBER, route.getRouteNumber());
                            i.putExtra(RouteActivity.ROUTE_ID, route.getRouteId());
                            i.putExtra(RouteActivity.TRIP_ID, route.getTripId());
                            i.putExtra(RouteActivity.STATION_ID, stationId);
                            startActivity(i);
                            finish();
                        }));

                        if (!isFinishing()) {
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    });

                } else {
                    // Auto-swap if there are only 2 trips
                    Route route = routes.get(routes.get(0).getTripId().equals(tripId) ? 1 : 0);
                    runOnUiThread(() -> {
                        Intent i = new Intent(this, RouteActivity.class);
                        i.putExtra(RouteActivity.ROUTE_NAME, route.getRouteName());
                        i.putExtra(RouteActivity.ROUTE_NUMBER, route.getRouteNumber());
                        i.putExtra(RouteActivity.ROUTE_ID, route.getRouteId());
                        i.putExtra(RouteActivity.TRIP_ID, route.getTripId());
                        i.putExtra(RouteActivity.STATION_ID, stationId);
                        startActivity(i);
                        finish();
                    });
                }

            } else runOnUiThread(() -> new CustomToast(this).showDefault(statusCode));
        }));

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

        bottom = findViewById(R.id.bottom_route);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root), (i, insets) -> {
            ViewGroup.MarginLayoutParams bottomParams = (ViewGroup.MarginLayoutParams) bottom.getLayoutParams();
            bottomParams.setMargins(0, insets.getSystemWindowInsetTop(), 0, 0);
            bottom.setLayoutParams(bottomParams);
            return insets.consumeSystemWindowInsets();
        });
    }

    private void initElements() {
        progressBar = findViewById(R.id.progress_bar);
        errorText = findViewById(R.id.tv_error);
        errorImageView = findViewById(R.id.iv_error);
        tryAgainText = findViewById(R.id.tv_try_again);
        errorContainer = findViewById(R.id.ll_error_container);
        backBtn = findViewById(R.id.back);
        number = findViewById(R.id.route_station_number);
        name = findViewById(R.id.route_name);
        circle = findViewById(R.id.route_circle);
        routeLoading = findViewById(R.id.loading_msg);
        recyclerView = findViewById(R.id.station_list);
        header = findViewById(R.id.header);
        bottomSheet = findViewById(R.id.bottom_sheet);
        opposite = findViewById(R.id.route_opposite_btn);
        shadow = findViewById(R.id.shadow);
    }

    private void setErrorUi(String errorName, int errorIconCode, int errorCode) {
        runOnUiThread(() -> {
            errorText.setText(errorName);
            errorImageView.setImageResource(errorIconCode);
            this.routeLoading.showMsgDefault(getApplicationContext(), errorCode);
        });
    }

    private void setupUi(ScreenState screenState) {
        runOnUiThread(() -> {
            switch (screenState) {
                case DONE: {
                    this.progressBar.setVisibility(View.GONE);
                    this.recyclerView.setVisibility(View.VISIBLE);
                    this.errorContainer.setVisibility(View.GONE);
                    this.routeLoading.showLoading(false);

                    break;
                }
                case LOADING: {
                    this.progressBar.setVisibility(View.VISIBLE);
                    this.recyclerView.setVisibility(View.GONE);
                    this.errorContainer.setVisibility(View.GONE);
                    this.routeLoading.showLoading(true);
                    break;
                }
                case ERROR: {
                    this.progressBar.setVisibility(View.GONE);
                    this.recyclerView.setVisibility(View.GONE);
                    this.errorContainer.setVisibility(View.VISIBLE);
                    this.routeLoading.showLoading(false);
                    break;
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ViewGroupUtils.isDarkTheme(this) ? R.style.DarkTheme : R.style.WhiteTheme);
        setContentView(R.layout.activity_route);
        initElements();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        app = TravanaApp.getInstance();
        networkConnectivityManager = app.getNetworkConnectivityManager();

        handler = new Handler(Looper.myLooper());

        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        hour = sharedPreferences.getBoolean("hour", false);

        // Get activity parameters
        routeName = getIntent().getStringExtra(ROUTE_NAME);
        routeNumber = getIntent().getStringExtra(ROUTE_NUMBER);
        routeId = getIntent().getStringExtra(ROUTE_ID);
        tripId = getIntent().getStringExtra(TRIP_ID);
        stationId = getIntent().getStringExtra(STATION_ID);
        if (stationId == null) stationId = "0";

        Api.addSavedSearchedItemsIds(tripId, this);

        setElements();

    }

    private void setMapPaddingBottom(Float offset) {
        float maxMapPaddingBottom = (float) behavior.getPeekHeight();
        setBottomPadding(Math.round(offset * maxMapPaddingBottom) + (int) maxMapPaddingBottom);
    }

    @Override
    protected void onPause() {
        super.onPause();
        startArrivalsUpdater();
        startBusesUpdater();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopArrivalsUpdater();
        stopBusesUpdater();
    }

    @Override
    public void onBackPressed() {

        // Collapse bottom sheet if expanded
        if (behavior.getState() == ViewPagerBottomSheetBehavior.STATE_EXPANDED)
            behavior.setState(ViewPagerBottomSheetBehavior.STATE_COLLAPSED);
        else super.onBackPressed();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);

        // Setup Google maps UI
        setPadding(0, 0, 0, behavior.getPeekHeight());
        setMapPaddingBottom(0f);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ljubljana, 11.5f));
        mMap.setOnMarkerClickListener(marker -> marker.getTitle() == null);
        mMap.setOnCameraIdleListener(() -> lastValidMapCenter = mMap.getCameraPosition().target);

        // Setup handlers.
        busManager = new BusMarkerManager(mMap, busOptions);

        // Set station InfoWindow click listener
        mMap.setOnInfoWindowClickListener(marker -> Api.stationDetails((String) marker.getTag(), true, (apiResponse, statusCode, success) -> {
            if (success) {
                // Start StationActivity
                Intent i = new Intent(this, StationActivity.class);
                i.putExtra(StationActivity.STATION, apiResponse.getData());
                startActivity(i);
            } else {
                // Show toast.
                runOnUiThread(() -> {
                    CustomToast customToast = new CustomToast(this);
                    customToast
                            .setTextColor(Color.WHITE)
                            .setIconColor(Color.WHITE)
                            .setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
                    customToast.showDefault(statusCode);
                });
            }
        }));
        updateBuses();
        updateArrivals();
        startBusesUpdater();
        startArrivalsUpdater();
    }

    class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        List<ArrivalOnRoute> stationsOnRoute = new ArrayList<>();
        int[][] isBus;

        private void setStationsOnRoute(List<ArrivalOnRoute> stationsOnRoute) {

            Map<String, Pair<Integer, Pair<Integer, Integer>>> toAnimateMap = new LinkedHashMap<>();
            for (int i = 0; i < stationsOnRoute.size(); i++) {
                List<ArrivalOnRoute.Arrival> arrivals = stationsOnRoute.get(i).getArrivals();
                int size = Math.min(arrivals.size(), 2);
                for (int j = 0; j < size; j++) {
                    ArrivalOnRoute.Arrival arrival = arrivals.get(j);
                    if (arrival.getType() != 3) {
                        if (!(arrival.getEtaMin() > 10 && arrival.getType() == 1)) {
                            Pair<Integer, Pair<Integer, Integer>> arrivalsToAnimate = toAnimateMap.get(arrival.getVehicleId());
                            if (arrivalsToAnimate == null)
                                toAnimateMap.put(arrival.getVehicleId(), new Pair<>(i, new Pair<>(j, arrival.getType())));
                        }
                    }
                }
            }

            isBus = new int[stationsOnRoute.size()][3];
            for (Map.Entry<String, Pair<Integer, Pair<Integer, Integer>>> entry : toAnimateMap.entrySet()) {
                Pair<Integer, Pair<Integer, Integer>> value = entry.getValue();
                int pos;
                if (value.second.second == 2) {
                    if (value.first < stationsOnRoute.size() - 1)
                        pos = value.first + 1;
                    else pos = value.first;

                } else pos = value.first;

                if (value.second.second == 2 || value.first == 0) {
                    int b = isBus[value.first][0]++;
                    if (b <= 1)
                        isBus[value.first][b + 1] = value.second.second;
                } else {
                    int b = isBus[value.first - 1][0]++;
                    if (b <= 1)
                        isBus[value.first - 1][b + 1] = value.second.second;
                }
            }

            this.stationsOnRoute = stationsOnRoute;

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(getLayoutInflater().inflate(R.layout.template_station_node, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            ArrivalOnRoute station = stationsOnRoute.get(position);

            // Get line color
            int color = Colors.getColorFromString(routeNumber);

            // Color line
            holder.topConnector.setBackgroundColor(color);
            holder.bottomConnector.setBackgroundColor(color);
            holder.node.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);

            // Set station name and text style
            holder.name.setText(station.getName());
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) holder.node.getLayoutParams();

            if (Integer.parseInt(stationId) == station.getStationCode()) {
                holder.name.setTypeface(null, Typeface.BOLD);
                holder.name.setTextSize(20f);
                params.height = 56;
                params.width = 56;
            } else {
                holder.name.setTypeface(null, Typeface.NORMAL);
                holder.name.setTextSize(14f);
                params.height = 32;
                params.width = 32;
            }
            holder.node.setLayoutParams(params);

            // Remove redundant top and bottom connectors
            holder.topConnector.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
            holder.bottomConnector.setVisibility(position == getItemCount() - 1 ? View.INVISIBLE : View.VISIBLE);

            // Set node click event
            holder.background.setOnClickListener(v -> Api.stationDetails(String.valueOf(station.getCode_id()), true, (apiResponse, statusCode, success) -> {
                if (success) {
                    // Start StationActivity
                    Intent i = new Intent(RouteActivity.this, StationActivity.class);
                    i.putExtra(StationActivity.STATION, apiResponse.getData());
                    startActivity(i);
                } else
                    runOnUiThread(() -> new CustomToast(RouteActivity.this).showDefault(statusCode));
            }));

            holder.liveArrivals.removeAllViews();

            if (isBus[position][0] > 0) {

                // Set bold and bigger text for previous activity station
                if (Integer.parseInt(stationId) == station.getStationCode()) {
                    params.height = 94;
                    params.width = 94;
                } else {
                    params.height = 76;
                    params.width = 76;
                }
                holder.node.setLayoutParams(params);
                if (isBus[position][0] == 2) {
                    if (isBus[position][1] == 1) {
                        if (isBus[position][2] == 1)
                            holder.node.setImageDrawable(ContextCompat.getDrawable(RouteActivity.this, R.drawable.bus_icon_3_offline3));
                        else
                            holder.node.setImageDrawable(ContextCompat.getDrawable(RouteActivity.this, R.drawable.bus_icon_3_offline1));
                    } else {
                        if (isBus[position][2] == 1)
                            holder.node.setImageDrawable(ContextCompat.getDrawable(RouteActivity.this, R.drawable.bus_icon_3_offline2));
                        else
                            holder.node.setImageDrawable(ContextCompat.getDrawable(RouteActivity.this, R.drawable.bus_icon_3));
                    }
                } else
                    holder.node.setImageDrawable(ContextCompat.getDrawable(RouteActivity.this, isBus[position][1] == 1 ? R.drawable.bus_icon_2_offline : R.drawable.bus_icon_2));
                holder.node.setColorFilter(null);
            } else {
                holder.node.setImageDrawable(ContextCompat.getDrawable(RouteActivity.this, R.drawable.station_circle));
            }

            List<ArrivalOnRoute.Arrival> arrivals = station.getArrivals();
            int size = Math.min(arrivals.size(), 2);
            if (size != 0)
                for (int i = 0; i < size; i++) {
                    ArrivalOnRoute.Arrival arrival = arrivals.get(i);

                    // Inflate view
                    View v = getLayoutInflater().inflate(R.layout.template_live_arrival_special, holder.liveArrivals, false);
                    TextView arrival_time = v.findViewById(R.id.arrival_time_time);
                    ImageView rss = v.findViewById(R.id.live_icon);
                    TextView garage = v.findViewById(R.id.garage_text);

                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());

                    // Set preferred time format
                    arrival_time.setText(hour ? formatter.format(DateTime.now().plusMinutes(arrival.getEtaMin()).toDate()) : String.format("%s min", arrival.getEtaMin()));
                    arrival_time.setTextColor(RouteActivity.this.color);
                    rss.setVisibility(View.INVISIBLE);
                    arrival_time.setTypeface(null, Typeface.NORMAL);

                    // (0 - predicted, 1 - scheduled, 2 - approaching station (prihod), 3 - detour (obvoz))
                    switch (arrival.getType()) {
                        case 0:
                            rss.setVisibility(View.VISIBLE);
                            break;
                        case 2:
                            arrival_time.setText(getString(R.string.arrival).toUpperCase());
                            arrival_time.setTypeface(null, Typeface.BOLD);
                            break;
                        case 3:
                            arrival_time.setText(getString(R.string.detour).toUpperCase());
                            arrival_time.setTypeface(null, Typeface.BOLD);
                            break;
                    }


                    if (arrival.getDepot() == 1)
                        garage.setVisibility(View.VISIBLE);
                    else garage.setVisibility(View.GONE);


                    // Ignore "ghost" arrivals
                    if (!arrival.getVehicleId().equals("22222222-2222-2222-2222-222222222222"))
                        holder.liveArrivals.addView(v);

                    // Show only one if type is "detour"
                    if (arrival.getType() == 3) break;

                }
            else {
                View v = getLayoutInflater().inflate(R.layout.template_live_arrival_special, holder.liveArrivals, false);
                TextView arrival_time = v.findViewById(R.id.arrival_time_time);

                arrival_time.setText("/");
                holder.liveArrivals.addView(v);

            }

        }

        @Override
        public int getItemCount() {
            return stationsOnRoute.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            View topConnector, bottomConnector, background;
            TextView name;
            ImageView node;
            FlexboxLayout liveArrivals;

            private ViewHolder(@NonNull View itemView) {
                super(itemView);

                topConnector = itemView.findViewById(R.id.top_node_connection);
                bottomConnector = itemView.findViewById(R.id.bottom_node_connection);
                node = itemView.findViewById(R.id.node_icon);
                background = itemView.findViewById(R.id.station_node_background);
                name = itemView.findViewById(R.id.station_name);
                liveArrivals = itemView.findViewById(R.id.station_arrivals);

            }
        }

    }

}
