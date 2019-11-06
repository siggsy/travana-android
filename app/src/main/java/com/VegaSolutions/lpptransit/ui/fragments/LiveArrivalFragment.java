package com.VegaSolutions.lpptransit.ui.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.ApiCallback;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.ApiResponse;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.ArrivalWrapper;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Route;
import com.VegaSolutions.lpptransit.ui.Colors;
import com.VegaSolutions.lpptransit.ui.activities.RouteActivity;
import com.VegaSolutions.lpptransit.ui.activities.StationActivity;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LiveArrivalFragment extends Fragment {

    private static final String STATION_ID = "station_id";

    private String stationId;
    private Context context;
    private FragmentHeaderCallback headerCallback;

    ApiCallback<ArrivalWrapper> callback = new ApiCallback<ArrivalWrapper>() {
        @Override
        public void onComplete(@Nullable ApiResponse<ArrivalWrapper> apiResponse, int statusCode, boolean success) {
            // TODO: handle error and no internet connection
            if (success) {
                ArrivalWrapper arrivalWrapper = apiResponse.getData();
                ((Activity)context).runOnUiThread(() -> {
                    adapter.setArrivals(RouteWrapper.getFromArrivals(arrivalWrapper.getArrivals()));
                    refreshLayout.setRefreshing(false);
                });
            }
        }
    };


    public LiveArrivalFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentHeaderCallback)
            headerCallback = (FragmentHeaderCallback) context;
        this.context = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        onHeaderChanged(rv.canScrollVertically(-1));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        headerCallback = null;
        context = null;
    }

    public static LiveArrivalFragment newInstance(String stationId) {
        LiveArrivalFragment fragment = new LiveArrivalFragment();
        Bundle args = new Bundle();
        args.putString(STATION_ID, stationId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            stationId = getArguments().getString(STATION_ID);
        }
    }

    private Adapter adapter;

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView rv;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_live_arrival, container, false);

        refreshLayout = root.findViewById(R.id.live_arrival_swipe_refresh);
        rv = root.findViewById(R.id.live_arrival_rv);

        adapter = new Adapter();
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.setAdapter(adapter);
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                onHeaderChanged(recyclerView.canScrollVertically(-1));
            }
        });

        refreshLayout.setRefreshing(true);
        refreshLayout.setOnRefreshListener(() -> {
            Api.arrival(stationId, callback);
        });
        refreshLayout.setColorSchemeColors(ContextCompat.getColor(context, R.color.colorAccent));

        Api.arrival(stationId, callback);

        return root;

    }

    private void onHeaderChanged(boolean value) {
        if (headerCallback != null)
            headerCallback.onHeaderChanged(value);
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
            viewHolder.number.setText(route.arrivalObject.getRoute_name());

            String group = route.arrivalObject.getRoute_name().replaceAll("[^0-9]", "");
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
                    case 3: arrival_event.setVisibility(View.GONE);
                        String detour_text = getResources().getString(R.string.detour).toUpperCase();
                        arrival_time.setText(detour_text);
                        arrival_time.setTextColor(Color.WHITE);
                        v.getBackground().setTint(ResourcesCompat.getColor(getResources(), R.color.event_detour, null));
                        break;
                    default: arrival_event.setVisibility(View.GONE);
                }
                if (!arrival.getVehicle_id().equals("22222222-2222-2222-2222-222222222222"))
                    viewHolder.arrivals.addView(v);
                if (arrival.getType() == 3) break;

            }

            viewHolder.route.setOnClickListener(v -> {
                Intent i = new Intent(context, RouteActivity.class);
                i.putExtra(RouteActivity.ROUTE_NAME, route.arrivalObject.getTrip_name());
                i.putExtra(RouteActivity.ROUTE_NUMBER, route.arrivalObject.getRoute_name());
                i.putExtra(RouteActivity.ROUTE_ID, route.arrivalObject.getRoute_id());
                i.putExtra(RouteActivity.TRIP_ID, route.arrivalObject.getTrip_id());
                startActivity(i);
            });



        }

        @Override
        public int getItemCount() {
            return routes.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            TextView name, number;
            View circle;

            LinearLayout route;
            FlexboxLayout arrivals;

            private ViewHolder(@NonNull View itemView) {
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
        ArrivalWrapper.Arrival arrivalObject;

        private static List<RouteWrapper> getFromArrivals(List<ArrivalWrapper.Arrival> arrivals) {

            Collections.sort(arrivals, (o1, o2) -> {
                String o1S = o1.getRoute_name().replaceAll("[^0-9]", "");
                String o2S = o2.getRoute_name().replaceAll("[^0-9]", "");
                int o1V = Integer.valueOf(o1S);
                int o2V = Integer.valueOf(o2S);
                if (o1V == o2V) return o1.getRoute_name().compareTo(o2.getRoute_name());
                return Integer.compare(o1V, o2V);
            });

            Map<String, RouteWrapper> map = new LinkedHashMap<>();

            for (ArrivalWrapper.Arrival arrival : arrivals) {

                RouteWrapper route = map.get(arrival.getRoute_name());
                if (route == null) {
                    route = new RouteWrapper();
                    route.name = arrival.getStations() != null ? arrival.getStations().getArrival() : arrival.getTrip_name();
                    route.arrivalObject = arrival;
                    map.put(arrival.getRoute_name(), route);
                }
                route.arrivals.add(arrival);

            }

            return new ArrayList<>(map.values());

        }

    }

}
