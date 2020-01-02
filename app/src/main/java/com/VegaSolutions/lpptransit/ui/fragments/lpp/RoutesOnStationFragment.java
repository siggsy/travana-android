package com.VegaSolutions.lpptransit.ui.fragments.lpp;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.RouteOnStation;
import com.VegaSolutions.lpptransit.ui.fragments.FragmentHeaderCallback;
import com.VegaSolutions.lpptransit.utility.Colors;
import com.VegaSolutions.lpptransit.ui.activities.lpp.DepartureActivity;
import com.VegaSolutions.lpptransit.ui.activities.lpp.RouteActivity;
import com.VegaSolutions.lpptransit.ui.errorhandlers.CustomToast;

import java.util.ArrayList;
import java.util.List;

public class RoutesOnStationFragment extends Fragment {


    private static final String STATION_ID = "station_id";
    private static final String STATION_NAME = "station_name";

    // Activity parameters
    private String stationId;
    private String stationName;

    private Context context;
    private FragmentHeaderCallback headerCallback;

    // Activity UI elements
    private RecyclerView rv;
    private ProgressBar progressBar;
    private Adapter adapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentHeaderCallback)
            headerCallback = (FragmentHeaderCallback) context;
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        headerCallback = null;
        context = null;
    }

    public static RoutesOnStationFragment newInstance(String stationId, String stationName) {
        Bundle args = new Bundle();
        args.putString(STATION_ID, stationId);
        args.putString(STATION_NAME, stationName);
        RoutesOnStationFragment fragment = new RoutesOnStationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            stationId = getArguments().getString(STATION_ID);
            stationName = getArguments().getString(STATION_NAME);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Update header
        onHeaderChanged(rv.canScrollVertically(-1));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_routes_on_station, container, false);

        adapter = new Adapter();

        // Find UI elements by id
        rv = root.findViewById(R.id.routes_on_station_rv);
        progressBar = root.findViewById(R.id.progressBar);


        // Setup UI
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                onHeaderChanged(rv.canScrollVertically(-1));
            }
        });

        // Query all routes on station
        Api.routesOnStation(Integer.valueOf(stationId), (apiResponse, statusCode, success) -> {

            // Cancel UI update if fragment not attached
            if (context == null)
                return;

            // Update UI
            ((Activity)context).runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);

                if (success) adapter.setRoutes(apiResponse.getData());
                else new CustomToast(context).showDefault(statusCode);

            });

        });

        return root;
    }

    private void onHeaderChanged(boolean value) {
        if (headerCallback != null)
            headerCallback.onHeaderChanged(value);
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        List<RouteOnStation> routes = new ArrayList<>();

        public void setRoutes(List<RouteOnStation> routes) {
            this.routes = routes;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(getLayoutInflater().inflate(R.layout.template_route, parent, false));
        }

        @Override
        public int getItemCount() {
            return routes.size();
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            RouteOnStation route = routes.get(position);

            // Set route name and number
            holder.name.setText(route.getRoute_group_name());
            holder.number.setText(route.getRoute_number());

            // Set route color
            holder.circle.getBackground().setTint(Colors.getColorFromString(route.getRoute_number()));

            // Start DepartureActivity on click
            holder.departure.setOnClickListener(v -> {
                Intent intent = new Intent(context, DepartureActivity.class);
                intent.putExtra(DepartureActivity.ROUTE_NAME, route.getRoute_group_name());
                intent.putExtra(DepartureActivity.ROUTE_NUMBER, route.getRoute_number());
                intent.putExtra(DepartureActivity.STATION_NAME, stationName);
                intent.putExtra(DepartureActivity.STATION_CODE, stationId);
                startActivity(intent);
            });

            // Start RouteActivity on click
            holder.map.setOnClickListener(v -> {
                Intent intent = new Intent(context, RouteActivity.class);
                intent.putExtra(RouteActivity.ROUTE_NAME, route.getRoute_group_name());
                intent.putExtra(RouteActivity.ROUTE_NUMBER, route.getRoute_number());
                intent.putExtra(RouteActivity.ROUTE_ID, route.getRoute_id());
                intent.putExtra(RouteActivity.TRIP_ID, route.getTrip_id());
                intent.putExtra(RouteActivity.STATION_ID, stationId);
                startActivity(intent);
            });

        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            TextView name, number;
            View circle;

            ImageView departure;
            ImageView map;

            LinearLayout container;

            private ViewHolder(@NonNull View itemView) {
                super(itemView);

                name = itemView.findViewById(R.id.route_name);
                number = itemView.findViewById(R.id.route_station_number);
                circle = itemView.findViewById(R.id.route_station_circle);
                departure = itemView.findViewById(R.id.route_departure_btn);
                map = itemView.findViewById(R.id.route_map_btn);

                container = itemView.findViewById(R.id.route_container);

                number.setTextSize(16f);

            }
        }

    }

}
