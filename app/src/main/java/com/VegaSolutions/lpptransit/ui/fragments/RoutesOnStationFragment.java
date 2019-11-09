package com.VegaSolutions.lpptransit.ui.fragments;


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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.RouteOnStation;
import com.VegaSolutions.lpptransit.ui.Colors;
import com.VegaSolutions.lpptransit.ui.activities.DepartureActivity;
import com.VegaSolutions.lpptransit.ui.activities.RouteActivity;

import java.util.ArrayList;
import java.util.List;

public class RoutesOnStationFragment extends Fragment {


    private static final String STATION_ID = "station_id";
    private static final String STATION_NAME = "station_name";

    private String stationId;
    private String stationName;
    private Context context;
    private FragmentHeaderCallback headerCallback;

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


    public RoutesOnStationFragment() {

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

    private RecyclerView rv;
    private Adapter adapter;

    @Override
    public void onResume() {
        super.onResume();
        onHeaderChanged(rv.canScrollVertically(-1));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_routes_on_station, container, false);

        adapter = new Adapter();

        rv = root.findViewById(R.id.routes_on_station_rv);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                onHeaderChanged(rv.canScrollVertically(-1));
            }
        });

        Api.routesOnStation(Integer.valueOf(stationId), (apiResponse, statusCode, success) -> {
            if (success) {
                ((Activity)context).runOnUiThread(() -> adapter.setRoutes(apiResponse.getData()));
            }
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

            holder.name.setText(route.getRoute_group_name());
            holder.number.setText(route.getRoute_number());
            String group = route.getRoute_number().replaceAll("[^0-9]", "");
            int color = Integer.valueOf(group);
            holder.circle.getBackground().setTint(Colors.colors.get(color));

            holder.departure.setOnClickListener(v -> {
                Intent intent = new Intent(context, DepartureActivity.class);
                intent.putExtra(DepartureActivity.ROUTE_NAME, route.getRoute_group_name());
                intent.putExtra(DepartureActivity.ROUTE_NUMBER, route.getRoute_number());
                intent.putExtra(DepartureActivity.STATION_NAME, stationName);
                intent.putExtra(DepartureActivity.STATION_CODE, stationId);
                startActivity(intent);
            });
            holder.map.setOnClickListener(v -> {
                Intent intent = new Intent(context, RouteActivity.class);
                intent.putExtra(RouteActivity.ROUTE_NAME, route.getRoute_group_name());
                intent.putExtra(RouteActivity.ROUTE_NUMBER, route.getRoute_number());
                intent.putExtra(RouteActivity.ROUTE_ID, route.getRoute_id());
                intent.putExtra(RouteActivity.TRIP_ID, route.getTrip_id());
                startActivity(intent);
            });

        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            TextView name, number;
            View circle;

            LinearLayout container;

            ImageView departure;
            ImageView map;


            public ViewHolder(@NonNull View itemView) {
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
