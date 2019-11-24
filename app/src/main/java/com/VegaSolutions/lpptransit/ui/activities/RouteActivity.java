package com.VegaSolutions.lpptransit.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
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
import com.VegaSolutions.lpptransit.lppapi.responseobjects.BusOnRoute;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Route;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.StationOnRoute;
import com.VegaSolutions.lpptransit.ui.animations.ElevationAnimation;
import com.VegaSolutions.lpptransit.utility.Colors;
import com.VegaSolutions.lpptransit.ui.custommaps.BusMarkerManager;
import com.VegaSolutions.lpptransit.ui.errorhandlers.CustomToast;
import com.VegaSolutions.lpptransit.ui.errorhandlers.TopMessage;
import com.VegaSolutions.lpptransit.utility.MapUtility;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RouteActivity extends FragmentActivity implements OnMapReadyCallback {


    public static final String ROUTE_NAME = "route_name";
    public static final String ROUTE_NUMBER = "route_number";
    public static final String ROUTE_ID = "route_id";
    public static final String TRIP_ID = "trip_id";
    public static final String STATION_ID = "station_id";

    private String routeName;
    private String routeNumber;
    private String routeId;
    private String tripId;
    private String stationId;

    private ImageView backBtn, locBtn;
    private TextView number;
    private TextView name;
    private View circle;
    private TopMessage route_loading;
    private RecyclerView recyclerView;
    private Adapter adapter;
    private View header;
    private View bottom_sheet;
    private BottomSheetBehavior behavior;
    private ImageButton opposite;

    private ElevationAnimation elevationAnimation;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private final int UPDATE_TIME = 2000;
    private LatLng ljubljana = new LatLng(46.056319, 14.505381);
    private GoogleMap mMap;
    private Handler handler;
    private BusMarkerManager busManager;
    private MarkerOptions busOptions;
    private MarkerOptions stationOptions;


    private ApiCallback<List<BusOnRoute>> busQuery = new ApiCallback<List<BusOnRoute>>() {
        @Override
        public void onComplete(@Nullable ApiResponse<List<BusOnRoute>> apiResponse, int statusCode, boolean success) {
            if (success) {
                List<BusOnRoute> buses = new ArrayList<>();

                runOnUiThread(() -> route_loading.showLoading(false));

                // Filter by trip ID.
                for (BusOnRoute busOnRoute : apiResponse.getData())
                    if (busOnRoute.getTrip_id().equals(tripId))
                        runOnUiThread(() -> buses.add(busOnRoute));

                // Update markers.
                runOnUiThread(() -> busManager.update(buses));
                handler.postDelayed(runnable, UPDATE_TIME);
            }
            else {
                handler.removeCallbacks(runnable);
                runOnUiThread(() -> route_loading.showMsgDefault(RouteActivity.this, statusCode));
            }
        }
    };

    private Runnable runnable = () -> Api.busesOnRoute(routeNumber, busQuery);

    private ApiCallback<List<StationOnRoute>> callback = (apiResponse, statusCode, success) -> {


        if (success) {
            // Sort stations.
            Collections.sort(apiResponse.getData(), (o1, o2) -> Integer.compare(o1.getOrder_no(), o2.getOrder_no()));
            adapter.stationsOnRoute = apiResponse.getData();
            runOnUiThread(() -> adapter.notifyDataSetChanged());
            List<LatLng> latLngs = new ArrayList<>();
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            // Add station markers.
            for (StationOnRoute stationOnRoute : apiResponse.getData()) {
                LatLng latLng = stationOnRoute.getLatLng();
                latLngs.add(latLng);
                builder.include(latLng);
                runOnUiThread(() -> {
                    Marker marker = mMap.addMarker(stationOptions.position(latLng).title(stationOnRoute.getName()));
                    marker.setTag(String.valueOf(stationOnRoute.getCode_id()));
                    if (Integer.valueOf(stationId)/10 == stationOnRoute.getCode_id()/10) {
                        marker.showInfoWindow();
                    }
                });
            }

            // Connect stations with polyline and move the camera.
            if (!apiResponse.getData().isEmpty()) {
                LatLngBounds bounds = builder.build();
                runOnUiThread(() -> {
                    mMap.addPolyline(new PolylineOptions().addAll(latLngs).width(14f).color(Colors.getColorFromString(routeNumber))); // ViewGroupUtils.isDarkTheme(this) ? Color.WHITE : Color.BLACK
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
                });
            } else {
                runOnUiThread(() -> {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ljubljana, 11.5f));
                });
            }

            // Start bus updater.
            handler.post(runnable);

        } else {
            runOnUiThread(() -> {
                route_loading.showMsgDefault(RouteActivity.this, statusCode);
            });
        }
    };



    private void setupUI() {

        elevationAnimation = new ElevationAnimation(header, 16);

        // Get drawable resource for markers.
        int color = Colors.getColorFromString(routeNumber);

        View v = getLayoutInflater().inflate(R.layout.station_node_maps, null);
        v.findViewById(R.id.stroke).getBackground().setTint(color);
        v.findViewById(R.id.solid).getBackground().setTint(ContextCompat.getColor(this, ViewGroupUtils.isDarkTheme(this) ? R.color.color_main_background_dark : R.color.color_main_background));

        IconGenerator generator = new IconGenerator(this);
        generator.setBackground(null);
        generator.setContentView(v);

        busOptions = new MarkerOptions().anchor(0.5f, 0.5f).zIndex(1f).icon(MapUtility.getMarkerIconFromDrawable(ContextCompat.getDrawable(this, R.drawable.ic_bus_24dp))).flat(true);
        stationOptions = new MarkerOptions().anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromBitmap(generator.makeIcon())).flat(true); // MapUtility.getMarkerIconFromDrawable(ContextCompat.getDrawable(this,R.drawable.station_circle))

        // Setup views.
        backBtn.setOnClickListener(vi -> super.onBackPressed());
        //names.setText(routeName);
        number.setText(routeNumber);
        number.setTextSize(14f);
        circle.getBackground().setTint(Colors.getColorFromString(routeNumber));
        route_loading.showLoading(true);
        route_loading.setErrorMsgBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
        route_loading.setErrorMsgColor(Color.WHITE);
        route_loading.setErrorIconColor(Color.WHITE);
        route_loading.setRefreshClickEvent(vi -> {
            route_loading.showLoading(true);
            Api.stationsOnRoute(tripId, callback);
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

        opposite.setOnClickListener(vi -> {

            Api.routes(routeId, (apiResponse, statusCode, success) -> {
                if (success) {
                    List<Route> routes = apiResponse.getData();
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

                } else {
                    runOnUiThread(() -> new CustomToast(this).showDefault(this, Toast.LENGTH_SHORT));
                }
            });


        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ViewGroupUtils.isDarkTheme(this) ? R.style.DarkTheme : R.style.WhiteTheme);
        setContentView(R.layout.activity_route);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        routeName = getIntent().getStringExtra(ROUTE_NAME);
        routeNumber = getIntent().getStringExtra(ROUTE_NUMBER);
        routeId = getIntent().getStringExtra(ROUTE_ID);
        tripId = getIntent().getStringExtra(TRIP_ID);
        stationId = getIntent().getStringExtra(STATION_ID);
        if (stationId == null) stationId = "0";

        backBtn = findViewById(R.id.back);
        name = findViewById(R.id.route_name);
        number = findViewById(R.id.route_station_number);
        circle = findViewById(R.id.route_circle);
        route_loading = findViewById(R.id.loading_msg);
        locBtn = findViewById(R.id.maps_location_icon);
        recyclerView = findViewById(R.id.station_list);
        header = findViewById(R.id.header);
        bottom_sheet = findViewById(R.id.bottom_sheet);
        opposite = findViewById(R.id.route_opposite_btn);

        behavior = BottomSheetBehavior.from(bottom_sheet);


        setupUI();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (handler != null)
            handler.postDelayed(runnable, UPDATE_TIME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onBackPressed() {
        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else super.onBackPressed();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();

        handler = new Handler();
        busManager = new BusMarkerManager(mMap, busOptions);

        // Setup map.
        mMap.setOnInfoWindowClickListener(marker -> {
            Api.stationDetails(Integer.valueOf((String) marker.getTag()), true, (apiResponse, statusCode, success) -> {
                if (success) {
                    Intent i = new Intent(this, StationActivity.class);
                    i.putExtra(StationActivity.STATION, apiResponse.getData());
                    startActivity(i);
                } else {
                    runOnUiThread(() -> {
                        CustomToast customToast = new CustomToast(this);
                        customToast
                                .setTextColor(Color.WHITE)
                                .setIconColor(Color.WHITE)
                                .setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
                        customToast.showDefault(this, statusCode);
                    });

                }
            });

        });
        mMap.setOnMarkerClickListener(marker -> marker.getTitle() == null);
        mMap.setPadding(0, 0, 0, behavior.getPeekHeight());
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        if (MapUtility.checkLocationPermission(this)) {
            locBtn.setOnClickListener(v -> fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    if (location != null)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15f));
                }
            }));
            mMap.setMyLocationEnabled(true);
        }

        if (ViewGroupUtils.isDarkTheme(this))
            mMap.setMapStyle(new MapStyleOptions(getString(R.string.dark_2)));
        else
            mMap.setMapStyle(new MapStyleOptions(getString(R.string.white)));

        // Query stations on route and display them on the map.
        Api.stationsOnRoute(tripId, callback);

    }

    class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {


        List<StationOnRoute> stationsOnRoute = new ArrayList<>();

        public void setStationsOnRoute(List<StationOnRoute> stationsOnRoute) {
            this.stationsOnRoute = stationsOnRoute;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(getLayoutInflater().inflate(R.layout.template_station_node, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            StationOnRoute station = stationsOnRoute.get(position);

            int color = Colors.getColorFromString(routeNumber);

            holder.topConnector.getBackground().setTint(color);
            holder.bottomConnector.getBackground().setTint(color);
            holder.node.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);

            holder.name.setText(station.getName());
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) holder.node.getLayoutParams();
            if (Integer.valueOf(stationId)/10 == station.getCode_id()/10) {
                holder.name.setTypeface(null, Typeface.BOLD);
                holder.name.setTextSize(18f);
                params.height = 64;
                params.width = 64;
            } else {
                holder.name.setTypeface(null, Typeface.NORMAL);
                holder.name.setTextSize(14f);
                params.height = 32;
                params.width = 32;
            }
            holder.node.setLayoutParams(params);

            holder.topConnector.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
            holder.bottomConnector.setVisibility(position == getItemCount() - 1 ? View.INVISIBLE : View.VISIBLE);
            holder.background.setOnClickListener(v -> {
                Api.stationDetails(station.getCode_id(), true, (apiResponse, statusCode, success) -> {
                    if (success) {
                        Intent i = new Intent(RouteActivity.this, StationActivity.class);
                        i.putExtra(StationActivity.STATION, apiResponse.getData());
                        startActivity(i);
                    } else {
                        runOnUiThread(() -> {
                            CustomToast customToast = new CustomToast(RouteActivity.this);
                            customToast
                                    .setTextColor(Color.WHITE)
                                    .setIconColor(Color.WHITE)
                                    .setBackgroundColor(ContextCompat.getColor(RouteActivity.this, R.color.colorAccent));
                            customToast.showDefault(RouteActivity.this, statusCode);
                        });

                    }
                });
            });

        }

        @Override
        public int getItemCount() {
            return stationsOnRoute.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            View topConnector, bottomConnector, background;
            TextView name;
            ImageView node;

            private ViewHolder(@NonNull View itemView) {
                super(itemView);

                topConnector = itemView.findViewById(R.id.top_node_connection);
                bottomConnector = itemView.findViewById(R.id.bottom_node_connection);
                node = itemView.findViewById(R.id.node_icon);
                background = itemView.findViewById(R.id.station_node_background);
                name = itemView.findViewById(R.id.station_name);

            }
        }

    }


}
