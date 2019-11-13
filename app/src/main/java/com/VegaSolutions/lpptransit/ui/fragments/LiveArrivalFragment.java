package com.VegaSolutions.lpptransit.ui.fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.ApiCallback;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.ArrivalWrapper;
import com.VegaSolutions.lpptransit.ui.Colors;
import com.VegaSolutions.lpptransit.ui.activities.RouteActivity;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;
import com.google.android.flexbox.FlexboxLayout;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class LiveArrivalFragment extends Fragment {

    private static final String STATION_ID = "station_id";

    private String stationId;
    private Context context;
    private FragmentHeaderCallback headerCallback;

    private Adapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView rv;
    private View no_arr_err;

    private boolean hour;

    private ApiCallback<ArrivalWrapper> callback = (apiResponse, statusCode, success) -> {
            // TODO: handle error and no internet connection
            if (success) {
                ArrivalWrapper arrivalWrapper = apiResponse.getData();
                if (context != null)
                    ((Activity)context).runOnUiThread(() -> {
                        if (apiResponse.getData().getArrivals().isEmpty())
                            no_arr_err.setVisibility(View.VISIBLE);
                        else
                            no_arr_err.setVisibility(View.GONE);
                        adapter.setArrivals(RouteWrapper.getFromArrivals(arrivalWrapper.getArrivals()));
                        refreshLayout.setRefreshing(false);
                    });
            }

    };

    private void setupUI() {

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
        refreshLayout.setOnRefreshListener(() -> Api.arrival(stationId, callback));
        refreshLayout.setColorSchemeColors(ContextCompat.getColor(context, R.color.colorAccent));

    }

    public static LiveArrivalFragment newInstance(String stationId) {
        LiveArrivalFragment fragment = new LiveArrivalFragment();
        Bundle args = new Bundle();
        args.putString(STATION_ID, stationId);
        fragment.setArguments(args);
        return fragment;
    }

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

        refreshLayout = root.findViewById(R.id.live_arrival_swipe_refresh);
        rv = root.findViewById(R.id.live_arrival_rv);
        no_arr_err = root.findViewById(R.id.live_arrival_no_arrivals_error);
        setupUI();

        // Query arrivals.
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
                startActivity(i);
            });

            // Set live arrivals.
            viewHolder.arrivals.removeAllViews();
            for (ArrivalWrapper.Arrival arrival : route.arrivals) {

                View v = getLayoutInflater().inflate(R.layout.template_arrival_time, viewHolder.arrivals, false);
                TextView arrival_time = v.findViewById(R.id.arrival_time_time);
                TextView arrival_event = v.findViewById(R.id.arrival_time_event);
                ImageView arrival_event_icon = v.findViewById(R.id.arrival_time_event_rss);
                View back = v.findViewById(R.id.arrival_time_back);

                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                arrival_time.setText(hour ? formatter.format(DateTime.now().plusMinutes(arrival.getEta_min()).toDate()) : String.format("%s min", String.valueOf(arrival.getEta_min())));
                int[] attribute = new int[] { android.R.attr.textColor, R.attr.backgroundViewColor };
                TypedArray array = context.obtainStyledAttributes(ViewGroupUtils.isDarkTheme(context) ? R.style.DarkTheme : R.style.WhiteTheme, attribute);
                @SuppressLint("ResourceType")
                int backColor = array.getColor(1, Color.WHITE);
                int color = array.getColor(0, Color.BLACK);
                arrival_time.setTextColor(color);
                back.getBackground().setTint(backColor);
                array.recycle();

                //(0 - predicted, 1 - scheduled, 2 - approaching station (prihod), 3 - detour (obvoz))

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
                if (!arrival.getVehicle_id().equals("22222222-2222-2222-2222-222222222222"))
                    viewHolder.arrivals.addView(v);
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
                    route.name = arrival.getStations() != null ? arrival.getStations().getArrival() : arrival.getTrip_name();
                    route.arrivalObject = arrival;
                    map.put(arrival.getTrip_id(), route);
                }
                route.arrivals.add(arrival);
            }
            return new ArrayList<>(map.values());

        }

    }

}
