package com.VegaSolutions.lpptransit.ui.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.ArrivalWrapper;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Station;
import com.VegaSolutions.lpptransit.ui.Colors;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StationActivity extends AppCompatActivity {


    TextView name, center;
    FrameLayout header;
    FloatingActionButton fab;
    RecyclerView recyclerView;
    ImageButton oppositeBtn;
    ImageView fav;

    String station_code;
    String station_name;
    boolean station_center;

    boolean favourite;

    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station);


        // Assign all UI elements
        fab = findViewById(R.id.station_refresh);
        name = findViewById(R.id.station_title);
        header = findViewById(R.id.header);
        center = findViewById(R.id.station_center);
        recyclerView = findViewById(R.id.station_routes_rv);
        oppositeBtn = findViewById(R.id.station_opposite_btn);

        // Get Intent data
        station_code = getIntent().getStringExtra("station_code");
        station_name = getIntent().getStringExtra("station_name");
        station_center = getIntent().getBooleanExtra("station_center", false);
        favourite = getSharedPreferences("station_favourites", MODE_PRIVATE).getBoolean(station_code, false);

        setupUI();

        // TODO: handle errors
        Api.arrival(station_code, (apiResponse, statusCode, success) -> {
            if (success) {
                if (apiResponse != null) {
                    ArrivalWrapper arrivalWrapper = apiResponse.getData();
                    runOnUiThread(() -> adapter.setArrivals(RouteWrapper.getFromArrivals(arrivalWrapper.getArrivals())));
                }
            }
        });

    }

    private class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<RouteWrapper> routes = new ArrayList<>();

        void setArrivals(List<RouteWrapper> routes) {
            this.routes = routes;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(getLayoutInflater().inflate(R.layout.template_live_arrival, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            RouteWrapper route = routes.get(position);
            ViewHolder viewHolder = (ViewHolder) holder;

            viewHolder.name.setText(route.name);
            viewHolder.number.setText(route.number);

            String group = route.number.replaceAll("[^0-9]", "");
            int color = Integer.valueOf(group);
            viewHolder.circle.getBackground().setTint(Colors.colors.get(color));

            viewHolder.arrivals.removeAllViews();
            for (ArrivalWrapper.Arrival arrival : route.arrivals) {
                View v = getLayoutInflater().inflate(R.layout.template_arrival_time, viewHolder.arrivals, false);
                TextView arrival_time = v.findViewById(R.id.arrival_time_time);
                TextView arrival_event = v.findViewById(R.id.arrival_time_event);
                v.getBackground().setTint(Color.WHITE);

                arrival_time.setText(String.format("%s min", String.valueOf(arrival.getEta_min())));
                arrival_time.setTextColor(Color.BLACK);

                //(0 - predicted, 1 - scheduled, 2 - approaching station (prihod), 3 - detour (obvoz))

                switch (arrival.getType()) {
                    case 0: arrival_event.setVisibility(View.VISIBLE);
                            arrival_event.setText("");
                            ViewGroup.LayoutParams params = arrival_event.getLayoutParams();
                            params.height = 32;
                            params.width = 32;
                            arrival_event.setLayoutParams(params);
                            arrival_event.getBackground().setTint(ResourcesCompat.getColor(getResources(), R.color.event_live, null));
                            break;
                    case 2: arrival_event.setVisibility(View.GONE);
                            String arrival_text = getResources().getString(R.string.arrival).toUpperCase();
                            arrival_time.setText(arrival_text);
                            arrival_time.setTextColor(Color.WHITE);
                            v.getBackground().setTint(ResourcesCompat.getColor(getResources(), R.color.event_arrival, null));
                            break;
                    case 3: arrival_event.setVisibility(View.VISIBLE);
                            arrival_event.setText(R.string.detour);
                            arrival_event.getBackground().setTint(ResourcesCompat.getColor(getResources(), R.color.event_detour, null));
                            break;
                    default: arrival_event.setVisibility(View.GONE);
                }

                viewHolder.arrivals.addView(v);
            }



        }

        @Override
        public int getItemCount() {
            return routes.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            TextView name, number;
            ConstraintLayout circle;

            LinearLayout route;
            FlexboxLayout arrivals;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);


                name = itemView.findViewById(R.id.live_arrival_route_name);
                number = itemView.findViewById(R.id.route_station_number);
                circle = itemView.findViewById(R.id.route_station_circle);
                arrivals = itemView.findViewById(R.id.live_arrival_arrivals);

                route = itemView.findViewById(R.id.live_arrival_ll);

            }
        }
    }

    private static class RouteWrapper {

        List<ArrivalWrapper.Arrival> arrivals = new ArrayList<>();
        String name;
        String number;

        public static List<RouteWrapper> getFromArrivals(List<ArrivalWrapper.Arrival> arrivals) {

            Map<String, RouteWrapper> map = new LinkedHashMap<>();

            for (ArrivalWrapper.Arrival arrival : arrivals) {

                RouteWrapper route = map.get(arrival.getRoute_name());
                if (route == null) {
                    route = new RouteWrapper();
                    route.name = arrival.getStations().getArrival();
                    route.number = arrival.getRoute_name();
                    map.put(arrival.getRoute_name(), route);
                }
                route.arrivals.add(arrival);

            }

            return new ArrayList<>(map.values());

        }

    }

    public void setupUI() {

        // Set header
        name.setText(station_name);
        center.setVisibility(station_center ? View.VISIBLE : View.GONE);
        fav = findViewById(R.id.station_favourite);

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
            if (Integer.valueOf(station_code) % 2 == 0) {
                Api.stationDetails(Integer.valueOf(station_code) - 1, true, (apiResponse, statusCode, success) -> {
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
                Log.i("station", "CENTER");
            } else {
                Api.stationDetails(Integer.valueOf(station_code) + 1, true, (apiResponse, statusCode, success) -> {
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
                Log.i("station", "NOT CENTER");
            }
        });


        adapter = new Adapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Header elevation
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            recyclerView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                header.setSelected(recyclerView.canScrollVertically(-1));
            });
        }

        fab.setOnClickListener(v -> {
            adapter.routes.clear();
            runOnUiThread(adapter::notifyDataSetChanged);
            Api.arrival(station_code, (apiResponse, statusCode, success) -> {
                if (success) {
                    ArrivalWrapper arrivalWrapper = apiResponse.getData();

                    runOnUiThread(() -> adapter.setArrivals(RouteWrapper.getFromArrivals(arrivalWrapper.getArrivals())));
                }
            });
        });
    }

}
