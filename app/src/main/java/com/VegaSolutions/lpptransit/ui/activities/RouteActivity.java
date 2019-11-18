package com.VegaSolutions.lpptransit.ui.activities;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.ApiCallback;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.ApiResponse;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.BusOnRoute;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.StationOnRoute;
import com.VegaSolutions.lpptransit.ui.Colors;
import com.VegaSolutions.lpptransit.ui.custommaps.BusMarkerManager;
import com.VegaSolutions.lpptransit.ui.errorhandlers.CustomToast;
import com.VegaSolutions.lpptransit.ui.errorhandlers.TopMessage;
import com.VegaSolutions.lpptransit.utility.MapUtility;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RouteActivity extends FragmentActivity implements OnMapReadyCallback {


    public static final String ROUTE_NAME = "route_name";
    public static final String ROUTE_NUMBER = "route_number";
    public static final String ROUTE_ID = "route_id";
    public static final String TRIP_ID = "trip_id";

    private String routeName;
    private String routeNumber;
    private String routeId;
    private String tripId;

    private ImageView backBtn;
    private TextView number;
    private TextView name;
    private View circle;
    private TopMessage route_loading;

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
            runOnUiThread(() -> route_loading.showLoading(false));
            // Sort stations.
            Collections.sort(apiResponse.getData(), (o1, o2) -> Integer.compare(o1.getOrder_no(), o2.getOrder_no()));
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
                });
            }

            // Connect stations with polyline and move the camera.
            if (!apiResponse.getData().isEmpty()) {
                LatLngBounds bounds = builder.build();
                runOnUiThread(() -> {
                    mMap.addPolyline(new PolylineOptions().addAll(latLngs).width(14f).color(ViewGroupUtils.isDarkTheme(this) ? Color.WHITE : Color.BLACK));
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

        // Get drawable resource for markers.
        busOptions = new MarkerOptions().anchor(0.5f, 0.5f).zIndex(1f).icon(MapUtility.getMarkerIconFromDrawable(ContextCompat.getDrawable(this, R.drawable.ic_bus_24dp))).flat(true);
        stationOptions = new MarkerOptions().anchor(0.5f, 0.5f).icon(MapUtility.getMarkerIconFromDrawable(ContextCompat.getDrawable(this,R.drawable.station_circle)));

        // Setup views.
        backBtn.setOnClickListener(v -> onBackPressed());
        //names.setText(routeName);
        number.setText(routeNumber);
        number.setTextSize(14f);
        circle.getBackground().setTint(Colors.getColorFromString(routeNumber));
        route_loading.showLoading(true);
        route_loading.setErrorMsgBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
        route_loading.setErrorMsgColor(Color.WHITE);
        route_loading.setErrorIconColor(Color.WHITE);
        route_loading.setRefreshClickEvent(v -> {
            route_loading.showLoading(true);
            Api.stationsOnRoute(tripId, callback);
        });
        name.setText(routeName);


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

        backBtn = findViewById(R.id.back);
        name = findViewById(R.id.route_name);
        number = findViewById(R.id.route_station_number);
        circle = findViewById(R.id.route_circle);
        route_loading = findViewById(R.id.loading_msg);


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
        mMap.setPadding(0, 200, 0, 0);
        mMap.setMyLocationEnabled(MapUtility.checkLocationPermission(this));
        if (ViewGroupUtils.isDarkTheme(this))
            mMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.dark)));

        // Query stations on route and display them on the map.
        Api.stationsOnRoute(tripId, callback);

    }


}
