package com.VegaSolutions.lpptransit.ui.fragments.lpp;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.ApiCallback;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.ArrivalWrapper;
import com.VegaSolutions.lpptransit.ui.fragments.FragmentHeaderCallback;
import com.VegaSolutions.lpptransit.utility.Colors;
import com.VegaSolutions.lpptransit.ui.activities.lpp.RouteActivity;
import com.VegaSolutions.lpptransit.ui.errorhandlers.CustomToast;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;
import com.google.android.flexbox.FlexboxLayout;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;

public class LiveArrivalFragment extends Fragment {

    private static final String STATION_ID = "station_id";

    private String stationId;
    private Context context;
    private FragmentHeaderCallback headerCallback;

    private Adapter adapter;

    @BindView(R.id.live_arrival_swipe_refresh) SwipeRefreshLayout refreshLayout;
    @BindView(R.id.live_arrival_rv) RecyclerView rv;
    @BindView(R.id.live_arrival_no_arrivals_error) View noArrErr;

    private boolean hour;
    private int color, backColor;

    private ApiCallback<ArrivalWrapper> callback = (apiResponse, statusCode, success) -> {

        // Cancel UI update if fragment is not attached to activity
        if (context == null)
            return;

        // Set UI
        ((Activity)context).runOnUiThread(() -> {
            refreshLayout.setRefreshing(false);
            if (success) {
                ArrivalWrapper arrivalWrapper = apiResponse.getData();

                // Check if arrival list is not empty and refresh rv adapter
                noArrErr.setVisibility(arrivalWrapper.getArrivals().isEmpty() ? View.VISIBLE : View.GONE);
                adapter.setArrivals(RouteWrapper.getFromArrivals(arrivalWrapper.getArrivals()));

            } else new CustomToast(context).showDefault(statusCode);
        });

    };

    private void setupUI() {

        adapter = new Adapter();
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.setAdapter(adapter);
        rv.setItemViewCacheSize(30);
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                onHeaderChanged(recyclerView.canScrollVertically(-1));
            }
        });

        refreshLayout.setRefreshing(true);
        refreshLayout.setOnRefreshListener(() -> Api.arrival(stationId, callback));
        refreshLayout.setColorSchemeColors(ContextCompat.getColor(context, R.color.colorAccent));

        // Save default theme colors
        int[] attribute = new int[] { android.R.attr.textColor, R.attr.backgroundViewColor };
        TypedArray array = context.obtainStyledAttributes(ViewGroupUtils.isDarkTheme(context) ? R.style.DarkTheme : R.style.WhiteTheme, attribute);
        backColor = array.getColor(1, Color.WHITE);
        color = array.getColor(0, Color.BLACK);
        array.recycle();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", MODE_PRIVATE);
        hour = sharedPreferences.getBoolean("hour", false);

        if (getArguments() != null) {
            stationId = getArguments().getString(STATION_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_live_arrival, container, false);
        ButterKnife.bind(this, root);

        setupUI();

        // Query arrivals.
        Api.arrival(stationId, callback);

        return root;

    }

    public static LiveArrivalFragment newInstance(String stationId) {
        LiveArrivalFragment fragment = new LiveArrivalFragment();
        Bundle args = new Bundle();
        args.putString(STATION_ID, stationId);
        fragment.setArguments(args);
        return fragment;
    }

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

    @Override
    public void onResume() {
        super.onResume();
        onHeaderChanged(rv.canScrollVertically(-1));
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

            // Update ViewHolder.
            viewHolder.name.setText(route.name);
            viewHolder.number.setText(route.arrivalObject.getRoute_name());
            viewHolder.circle.getBackground().setTint(Colors.getColorFromString(route.arrivalObject.getRoute_name()));
            viewHolder.route.setOnClickListener(v -> {
                Intent i = new Intent(context, RouteActivity.class);
                i.putExtra(RouteActivity.ROUTE_NAME, route.arrivalObject.getTrip_name());
                i.putExtra(RouteActivity.ROUTE_NUMBER, route.arrivalObject.getRoute_name());
                i.putExtra(RouteActivity.ROUTE_ID, route.arrivalObject.getRoute_id());
                i.putExtra(RouteActivity.TRIP_ID, route.arrivalObject.getTrip_id());
                i.putExtra(RouteActivity.STATION_ID, stationId);
                startActivity(i);
            });

            // Set live arrivals.
            viewHolder.arrivals.removeAllViews();
            for (ArrivalWrapper.Arrival arrival : route.arrivals) {

                // Inflate view
                View v = getLayoutInflater().inflate(R.layout.template_arrival_time, viewHolder.arrivals, false);
                TextView arrival_time = v.findViewById(R.id.arrival_time_time);
                TextView arrival_event = v.findViewById(R.id.arrival_time_event);
                ImageView arrival_event_icon = v.findViewById(R.id.arrival_time_event_rss);
                View back = v.findViewById(R.id.arrival_time_back);

                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");

                // Set preferred time format
                arrival_time.setText(hour ? formatter.format(DateTime.now().plusMinutes(arrival.getEta_min()).toDate()) : String.format("%s min", String.valueOf(arrival.getEta_min())));
                arrival_time.setTextColor(color);
                back.getBackground().setTint(backColor);

                // (0 - predicted, 1 - scheduled, 2 - approaching station (prihod), 3 - detour (obvoz))

                switch (arrival.getType()) {
                    case 0:
                        arrival_event_icon.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        arrival_event.setVisibility(View.GONE);
                        arrival_event_icon.setVisibility(View.GONE);
                        arrival_time.setText(getString(R.string.arrival).toUpperCase());
                        arrival_time.setTextColor(Color.WHITE);
                        back.getBackground().setTint(ResourcesCompat.getColor(getResources(), R.color.event_arrival, null));
                        break;
                    case 3:
                        arrival_event.setVisibility(View.GONE);
                        arrival_event_icon.setVisibility(View.GONE);
                        arrival_time.setText(getString(R.string.detour).toUpperCase());
                        arrival_time.setTextColor(Color.WHITE);
                        back.getBackground().setTint(ResourcesCompat.getColor(getResources(), R.color.event_detour, null));
                        break;
                    default:
                        arrival_event.setVisibility(View.GONE);
                        arrival_event_icon.setVisibility(View.GONE);
                }

                // Ignore "ghost" arrivals
                if (!arrival.getVehicle_id().equals("22222222-2222-2222-2222-222222222222"))
                    viewHolder.arrivals.addView(v);

                // Add "garage" flag
                if (arrival.getDepot() == 1) {
                    arrival_event.setText(getString(R.string.garage));
                    arrival_event.setTextColor(color);
                    arrival_event.getBackground().setTint(backColor);
                    arrival_event.setVisibility(View.VISIBLE);
                } else {
                    arrival_event.setText("");
                    arrival_event.getBackground().setTint(backColor);
                    arrival_event.setVisibility(View.GONE);
                }

                // Show only one if type is "detour"
                if (arrival.getType() == 3) break;

            }

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

            // Sort by route number
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
                RouteWrapper route = map.get(arrival.getTrip_id());
                if (route == null) {
                    route = new RouteWrapper();

                    ArrivalWrapper.Arrival.Stations stations = arrival.getStations();
                    route.name = stations != null && stations.getArrival() != null && !stations.getArrival().equals("") ? stations.getArrival() : arrival.getTrip_name();

                    route.arrivalObject = arrival;
                    map.put(arrival.getTrip_id(), route);
                }
                route.arrivals.add(arrival);
            }
            return new ArrayList<>(map.values());

        }

    }

}
