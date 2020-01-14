package com.VegaSolutions.lpptransit.ui.activities.lpp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.ApiCallback;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.ApiResponse;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.ArrivalOnRoute;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.ArrivalWrapper;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.BusOnRoute;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Route;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.StationOnRoute;
import com.VegaSolutions.lpptransit.ui.activities.MapFragmentActivity;
import com.VegaSolutions.lpptransit.ui.animations.ElevationAnimation;
import com.VegaSolutions.lpptransit.ui.custommaps.LocationMarkerManager;
import com.VegaSolutions.lpptransit.ui.custommaps.MyLocationManager;
import com.VegaSolutions.lpptransit.utility.Colors;
import com.VegaSolutions.lpptransit.ui.custommaps.BusMarkerManager;
import com.VegaSolutions.lpptransit.ui.errorhandlers.CustomToast;
import com.VegaSolutions.lpptransit.ui.errorhandlers.TopMessage;
import com.VegaSolutions.lpptransit.utility.MapUtility;
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
import com.google.maps.android.ui.IconGenerator;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RouteActivity extends MapFragmentActivity {


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
    @BindView(R.id.back) ImageView backBtn;
    @BindView(R.id.route_station_number) TextView number;
    @BindView(R.id.route_name) TextView name;
    @BindView(R.id.route_circle) View circle;
    @BindView(R.id.loading_msg) TopMessage routeLoading;
    @BindView(R.id.station_list) RecyclerView recyclerView;
    @BindView(R.id.header) View header;
    @BindView(R.id.bottom_sheet) View bottomSheet;
    @BindView(R.id.route_opposite_btn) ImageButton opposite;

    private ElevationAnimation elevationAnimation;
    private Adapter adapter;
    private BottomSheetBehavior behavior;

    // Google maps parameters
    private final int UPDATE_TIME = 8000;
    private LatLng ljubljana = new LatLng(46.056319, 14.505381);
    private Handler handler;
    private BusMarkerManager busManager;
    private MarkerOptions busOptions;
    private MarkerOptions stationOptions;

    private boolean hour;
    private int backColor;
    private int color;

    // Bus updater query
    private ApiCallback<List<BusOnRoute>> busQuery = new ApiCallback<List<BusOnRoute>>() {
        @Override
        public void onComplete(@Nullable ApiResponse<List<BusOnRoute>> apiResponse, int statusCode, boolean success) {
            runOnUiThread(() -> {
                if (success) {
                    List<BusOnRoute> buses = new ArrayList<>();
                    routeLoading.showLoading(false);

                    // Filter by trip ID.
                    for (BusOnRoute busOnRoute : apiResponse.getData())
                        if (busOnRoute.getTrip_id().equals(tripId))
                            buses.add(busOnRoute);

                    // Update markers and queue another update.
                    busManager.update(buses);


                } else {
                    // Remove update queue and show error message
                    handler.removeCallbacks(runnable);
                    routeLoading.showMsgDefault(RouteActivity.this, statusCode);
                }
            });
        }
    };

    // Stations on route query
    private ApiCallback<List<ArrivalOnRoute>> callback = new ApiCallback<List<ArrivalOnRoute>>() {
        @Override
        public void onComplete(@Nullable ApiResponse<List<ArrivalOnRoute>> apiResponse, int statusCode, boolean success) {

            if (success) {
                List<ArrivalOnRoute> stationsOnRoute = apiResponse.getData();

                // Sort stations.
                Collections.sort(stationsOnRoute, (o1, o2) -> Integer.compare(o1.getOrder_no(), o2.getOrder_no()));
                for (ArrivalOnRoute station : stationsOnRoute) {
                    Collections.sort(station.getArrivals(), (o1, o2) -> {
                        if (o1.getType() == 2) return -1;
                        else if (o2.getType() == 2) return 1;
                        else return Integer.compare(o1.getEta_min(), o2.getEta_min());
                    });
                }
                adapter.setStationsOnRoute(stationsOnRoute);
                RouteActivity.this.runOnUiThread(() -> adapter.notifyDataSetChanged());

                if (handler == null) {
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
                            if (Integer.valueOf(stationId) / 10 == stationOnRoute.getCode_id() / 10) {
                                marker.showInfoWindow();
                            }
                        });
                    }

                    // Connect stations with polyline and move the camera.
                    RouteActivity.this.runOnUiThread(() -> {
                        if (!stationsOnRoute.isEmpty()) {
                            LatLngBounds bounds = builder.build();

                            mMap.addPolyline(new PolylineOptions().addAll(latLngs).width(14f).color(Colors.getColorFromString(routeNumber))); // ViewGroupUtils.isDarkTheme(this) ? Color.WHITE : Color.BLACK
                            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));

                        } else mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ljubljana, 11.5f));
                    });

                    // Start bus updater.

                    runOnUiThread(() -> {
                        handler = new Handler();
                        handler.post(runnable);
                    });
                } else {
                    Api.busesOnRoute(routeNumber, busQuery);
                    handler.postDelayed(runnable, UPDATE_TIME);
                }

            } else {
                if (handler != null)
                    handler.removeCallbacks(runnable);
                RouteActivity.this.runOnUiThread(() -> routeLoading.showMsgDefault(RouteActivity.this, statusCode));
            }
        }
    };

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Api.arrivalsOnRoute(tripId, callback);
        }
    };





    private void setupUI() {

        // Header elevation animation
        elevationAnimation = new ElevationAnimation(header, 16);

        // Get drawable resource for markers.
        int color = Colors.getColorFromString(routeNumber);

        // Set bus and station marker style
        View v = getLayoutInflater().inflate(R.layout.station_node_maps, null);
        v.findViewById(R.id.stroke).getBackground().setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);
        v.findViewById(R.id.solid).getBackground().setColorFilter(ContextCompat.getColor(this, ViewGroupUtils.isDarkTheme(this) ? R.color.color_main_background_dark : R.color.color_main_background), PorterDuff.Mode.SRC_IN);
        IconGenerator generator = new IconGenerator(this);
        generator.setBackground(null);
        generator.setContentView(v);
        busOptions = new MarkerOptions().anchor(0.5f, 0.5f).zIndex(1f).icon(MapUtility.getMarkerIconFromDrawable(ContextCompat.getDrawable(this, R.drawable.ic_water_drop_svgrepo_com_1))).flat(true);
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
            routeLoading.showLoading(true);
            Api.arrivalsOnRoute(tripId, callback);
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
                        trips[i] = route.getRoute_name();
                    }

                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle(getString(R.string.select_route));
                        builder.setItems(trips, (dialog, which) -> runOnUiThread(() -> {
                            Route route = routes.get(which);
                            Intent i = new Intent(this, RouteActivity.class);
                            i.putExtra(RouteActivity.ROUTE_NAME, route.getRoute_name());
                            i.putExtra(RouteActivity.ROUTE_NUMBER, route.getRoute_number());
                            i.putExtra(RouteActivity.ROUTE_ID, route.getRoute_id());
                            i.putExtra(RouteActivity.TRIP_ID, route.getTrip_id());
                            i.putExtra(RouteActivity.STATION_ID, stationId);
                            startActivity(i);
                            finish();
                        }));
                        builder.show();
                    });

                } else {
                    // Auto-swap if there are only 2 trips
                    Route route = routes.get(routes.get(0).getTrip_id().equals(tripId) ? 1 : 0);
                    runOnUiThread(() -> {
                        Intent i = new Intent(this, RouteActivity.class);
                        i.putExtra(RouteActivity.ROUTE_NAME, route.getRoute_name());
                        i.putExtra(RouteActivity.ROUTE_NUMBER, route.getRoute_number());
                        i.putExtra(RouteActivity.ROUTE_ID, route.getRoute_id());
                        i.putExtra(RouteActivity.TRIP_ID, route.getTrip_id());
                        i.putExtra(RouteActivity.STATION_ID, stationId);
                        startActivity(i);
                        finish();
                    });
                }

            } else runOnUiThread(() -> new CustomToast(this).showDefault(statusCode));
        }));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ViewGroupUtils.isDarkTheme(this) ? R.style.DarkTheme : R.style.WhiteTheme);
        setContentView(R.layout.activity_route);
        ButterKnife.bind(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        hour = sharedPreferences.getBoolean("hour", false);

        // Get activity parameters
        routeName = getIntent().getStringExtra(ROUTE_NAME);
        routeNumber = getIntent().getStringExtra(ROUTE_NUMBER);
        routeId = getIntent().getStringExtra(ROUTE_ID);
        tripId = getIntent().getStringExtra(TRIP_ID);
        stationId = getIntent().getStringExtra(STATION_ID);
        if (stationId == null) stationId = "0";

        locationIcon = findViewById(R.id.maps_location_icon);

        Log.i("TRIP ID", tripId);

        behavior = BottomSheetBehavior.from(bottomSheet);

        int[] attribute = new int[] { android.R.attr.textColor, R.attr.backgroundViewColor };
        TypedArray array = obtainStyledAttributes(ViewGroupUtils.isDarkTheme(this) ? R.style.DarkTheme : R.style.WhiteTheme, attribute);
        backColor = array.getColor(1, Color.WHITE);
        color = ViewGroupUtils.isDarkTheme(this) ? Color.WHITE : Color.BLACK;
        array.recycle();

        setupUI();

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume bus updates
        if (handler != null)
            handler.post(runnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause bus updates
        if (handler != null)
            handler.removeCallbacks(runnable);
    }

    @Override
    public void onBackPressed() {

        // Collapse bottom sheet if expanded
        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else super.onBackPressed();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);

        // Setup Google maps UI
        mMap.setPadding(0, 0, 0, behavior.getPeekHeight());
        mMap.setOnMarkerClickListener(marker -> marker.getTitle() == null);

        // Setup handlers.
        busManager = new BusMarkerManager(mMap, busOptions);

        // Set station InfoWindow click listener
        mMap.setOnInfoWindowClickListener(marker -> Api.stationDetails(Integer.valueOf((String) marker.getTag()), true, (apiResponse, statusCode, success) -> {
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

        // Query stations on route and display them on the map.
        Api.arrivalsOnRoute(tripId, callback);

    }

    class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {


        List<ArrivalOnRoute> stationsOnRoute = new ArrayList<>();
        boolean[][] toAnimate;
        int[][] isBus;

        private void setStationsOnRoute(List<ArrivalOnRoute> stationsOnRoute) {

            Map<String, Pair<Integer, Pair<Integer, Integer>>> toAnimateMap = new LinkedHashMap<>();
            for (int i = 0; i < stationsOnRoute.size(); i++) {
                List<ArrivalOnRoute.Arrival> arrivals = stationsOnRoute.get(i).getArrivals();
                int size = arrivals.size() <= 2 ? arrivals.size() : 2;
                for (int j = 0; j < size; j++) {
                    ArrivalOnRoute.Arrival arrival = arrivals.get(j);
                    if (arrival.getType() != 3) {
                        if (!(arrival.getEta_min() > 8 && arrival.getType() == 1)) {
                            Pair<Integer, Pair<Integer, Integer>> arrivalsToAnimate = toAnimateMap.get(arrival.getVehicle_id());
                            if (arrivalsToAnimate == null)
                                toAnimateMap.put(arrival.getVehicle_id(), new Pair<>(i, new Pair<>(j, arrival.getType())));

                        }
                    }
                }
            }

            isBus = new int[stationsOnRoute.size()][3];
            toAnimate = new boolean[stationsOnRoute.size()][2];
            for (Map.Entry<String, Pair<Integer, Pair<Integer, Integer>>> entry : toAnimateMap.entrySet()) {
                Pair<Integer, Pair<Integer, Integer>> value = entry.getValue();
                int pos;
                if (value.second.second == 2) {
                    if (value.first < stationsOnRoute.size() - 1)
                        pos = value.first + 1;
                    else pos = value.first;

                } else pos = value.first;
                boolean[] tA = toAnimate[pos];

                if (tA == null) {
                    tA = new boolean[2];
                    toAnimate[pos] = tA;
                }

                int a = value.second.first;
                if (a < 2)
                    tA[a] = true;
                if (value.second.second == 2 || value.first == 0) {
                    int b = isBus[value.first][0]++;
                    isBus[value.first][b + 1] = value.second.second;
                }
                else {
                    int b = isBus[value.first - 1][0]++;
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
            holder.name.setTypeface(null, Typeface.BOLD);
            holder.name.setTextSize(14f);
            params.height = 32;
            params.width = 32;
            holder.node.setLayoutParams(params);

            // Remove redundant top and bottom connectors
            holder.topConnector.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
            holder.bottomConnector.setVisibility(position == getItemCount() - 1 ? View.INVISIBLE : View.VISIBLE);

            // Set node click event
            holder.background.setOnClickListener(v -> Api.stationDetails(station.getCode_id(), true, (apiResponse, statusCode, success) -> {
                if (success) {
                    // Start StationActivity
                    Intent i = new Intent(RouteActivity.this, StationActivity.class);
                    i.putExtra(StationActivity.STATION, apiResponse.getData());
                    startActivity(i);
                } else runOnUiThread(() -> new CustomToast(RouteActivity.this).showDefault(statusCode));
            }));

            holder.liveArrivals.removeAllViews();

            if (isBus[position][0] > 0) {

                // Set bold and bigger text for previous activity station
                holder.name.setTypeface(null, Typeface.BOLD);
                params.height = 80;
                params.width = 80;
                holder.node.setLayoutParams(params);
                if (isBus[position][0] == 2) {
                    if (isBus[position][1] == 1) {
                        if (isBus[position][2] == 1)
                            holder.node.setImageDrawable(ContextCompat.getDrawable(RouteActivity.this, R.drawable.bus_icon_3_offline3));
                        else holder.node.setImageDrawable(ContextCompat.getDrawable(RouteActivity.this, R.drawable.bus_icon_3_offline1));
                    } else {
                        if (isBus[position][2] == 1)
                            holder.node.setImageDrawable(ContextCompat.getDrawable(RouteActivity.this, R.drawable.bus_icon_3_offline2));
                        else holder.node.setImageDrawable(ContextCompat.getDrawable(RouteActivity.this, R.drawable.bus_icon_3));
                    }
                } else holder.node.setImageDrawable(ContextCompat.getDrawable(RouteActivity.this, isBus[position][1] == 1 ? R.drawable.bus_icon_2_offline : R.drawable.bus_icon_2));
                holder.node.setColorFilter(null);
            } else {
                holder.node.setImageDrawable(ContextCompat.getDrawable(RouteActivity.this, R.drawable.station_circle_node));
            }

            List<ArrivalOnRoute.Arrival> arrivals = station.getArrivals();
            int size = arrivals.size() <= 2 ? arrivals.size() : 2;
            if (size != 0)
                for (int i = 0; i < size; i++) {
                    ArrivalOnRoute.Arrival arrival = arrivals.get(i);

                    // Inflate view
                    View v = getLayoutInflater().inflate(R.layout.template_live_arrival_special, holder.liveArrivals, false);
                    TextView arrival_time = v.findViewById(R.id.arrival_time_time);
                    View back = v.findViewById(R.id.arrival_time_back);
                    ImageView rss = v.findViewById(R.id.live_icon);
                    TextView garage = v.findViewById(R.id.garage_text);

                    if (toAnimate[position][i]) {
                        AnimationDrawable drawable = (AnimationDrawable) ContextCompat.getDrawable(RouteActivity.this, R.drawable.rss_3layer);
                        rss.setImageDrawable(drawable);
                        drawable.start();
                    } else rss.setImageDrawable(ContextCompat.getDrawable(RouteActivity.this, R.drawable.ic_rss_feed_24px));

                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());

                    // Set preferred time format
                    arrival_time.setText(hour ? formatter.format(DateTime.now().plusMinutes(arrival.getEta_min()).toDate()) : String.format("%s min", String.valueOf(arrival.getEta_min())));
                    arrival_time.setTextColor(RouteActivity.this.color);
                    back.getBackground().setTint(backColor);
                    rss.setVisibility(View.GONE);
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
                    if (!arrival.getVehicle_id().equals("22222222-2222-2222-2222-222222222222"))
                        holder.liveArrivals.addView(v);

                    // Show only one if type is "detour"
                    if (arrival.getType() == 3) break;

                }
            else {
                View v = getLayoutInflater().inflate(R.layout.template_live_arrival_special, holder.liveArrivals, false);
                TextView arrival_time = v.findViewById(R.id.arrival_time_time);
                View back = v.findViewById(R.id.arrival_time_back);
                back.getBackground().setTint(backColor);

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
