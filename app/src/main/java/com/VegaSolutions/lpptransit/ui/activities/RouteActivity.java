package com.VegaSolutions.lpptransit.ui.activities;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.ApiCallback;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.ApiResponse;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.BusOnRoute;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.StationOnRoute;
import com.VegaSolutions.lpptransit.ui.Colors;
import com.VegaSolutions.lpptransit.utility.MapUtility;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RouteActivity extends FragmentActivity implements OnMapReadyCallback {


    public static final String ROUTE_NAME = "route_name";
    public static final String ROUTE_NUMBER = "route_number";
    public static final String ROUTE_ID = "route_id";
    public static final String TRIP_ID = "trip_id";

    private final int UPDATE_TIME = 1000;

    private String routeName;
    private String routeNumber;
    private String routeId;
    private String tripId;

    private ImageButton backBtn;
    private TextView name, number;
    private View circle;

    private LatLng ljubljana = new LatLng(46.056319, 14.505381);

    private GoogleMap mMap;
    private Handler handler;

    List<Marker> buses = new ArrayList<>();

    MarkerOptions busOptions;

    private ApiCallback<List<BusOnRoute>> busQuery = new ApiCallback<List<BusOnRoute>>() {
        @Override
        public void onComplete(@Nullable ApiResponse<List<BusOnRoute>> apiResponse, int statusCode, boolean success) {
            if (success) {
                for (Marker marker : buses)
                    runOnUiThread(marker::remove);
                buses.clear();
                for (BusOnRoute busOnRoute : apiResponse.getData())
                    if (busOnRoute.getTrip_id().equals(tripId))
                        runOnUiThread(() -> buses.add(mMap.addMarker(busOptions.position(busOnRoute.getLatLng()).rotation(busOnRoute.getCardinal_direction()))));
            }
        }
    };

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Api.busesOnRoute(routeNumber, busQuery);
            handler.postDelayed(this, UPDATE_TIME);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        busOptions = new MarkerOptions().icon(MapUtility.getMarkerIconFromDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_bus_24dp, null))).anchor(0.5f, 0.5f);

        routeName = getIntent().getStringExtra(ROUTE_NAME);
        routeNumber = getIntent().getStringExtra(ROUTE_NUMBER);
        routeId = getIntent().getStringExtra(ROUTE_ID);
        tripId = getIntent().getStringExtra(TRIP_ID);

        backBtn = findViewById(R.id.back);
        name = findViewById(R.id.route_name);
        number = findViewById(R.id.route_station_number);
        circle = findViewById(R.id.route_circle);


        backBtn.setOnClickListener(v -> onBackPressed());
        name.setText(routeName);
        number.setText(routeNumber);
        number.setTextSize(14f);

        String group = routeNumber.replaceAll("[^0-9]", "");
        int color = Integer.valueOf(group);
        circle.getBackground().setTint(Colors.colors.get(color));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        handler = new Handler();

        mMap.setOnInfoWindowClickListener(marker -> {
            Intent i = new Intent(this, StationActivity.class);
            i.putExtra(StationActivity.STATION_NAME, marker.getTitle());
            i.putExtra(StationActivity.STATION_CODE, marker.getSnippet());
            i.putExtra(StationActivity.STATION_CENTER, Integer.valueOf(marker.getSnippet()) % 2 != 0);
            startActivity(i);
        });

        mMap.setPadding(0, 200, 0, 0);

        Api.stationsOnRoute(tripId, (apiResponse, statusCode, success) -> {
            if (success) {

                BitmapDescriptor stationIcon = MapUtility.getMarkerIconFromDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.station_circle, null));

                Collections.sort(apiResponse.getData(), (o1, o2) -> Integer.compare(o1.getOrder_no(), o2.getOrder_no()));

                List<LatLng> latLngs = new ArrayList<>();
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (StationOnRoute stationOnRoute : apiResponse.getData()) {
                    LatLng latLng = stationOnRoute.getLatLng();
                    latLngs.add(latLng);
                    builder.include(latLng);
                    runOnUiThread(() -> mMap.addMarker(new MarkerOptions().position(latLng).title(stationOnRoute.getName()).snippet(String.valueOf(stationOnRoute.getCode_id())).icon(stationIcon).anchor(0.5f, 0.5f)));
                }
                if (!apiResponse.getData().isEmpty()) {
                    LatLngBounds bounds = builder.build();
                    runOnUiThread(() -> {
                        mMap.addPolyline(new PolylineOptions().addAll(latLngs).width(7f));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
                    });
                } else {
                    runOnUiThread(() -> {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ljubljana, 11.5f));
                    });
                }

                handler.postDelayed(runnable, UPDATE_TIME);

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (handler != null)
            handler.postDelayed(runnable, UPDATE_TIME);
    }
}
