package com.VegaSolutions.lpptransit.ui.fragments.lpp;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.TravanaApp;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.RouteOnStation;
import com.VegaSolutions.lpptransit.ui.activities.DepartureActivity;
import com.VegaSolutions.lpptransit.ui.activities.RouteActivity;
import com.VegaSolutions.lpptransit.ui.fragments.FragmentHeaderCallback;
import com.VegaSolutions.lpptransit.utility.Colors;
import com.VegaSolutions.lpptransit.utility.NetworkConnectivityManager;
import com.VegaSolutions.lpptransit.utility.ScreenState;

import java.util.ArrayList;
import java.util.List;

import static com.VegaSolutions.lpptransit.utility.ScreenState.DONE;
import static com.VegaSolutions.lpptransit.utility.ScreenState.ERROR;
import static com.VegaSolutions.lpptransit.utility.ScreenState.LOADING;

public class RoutesOnStationFragment extends Fragment {


    private static final String STATION_ID = "station_id";
    private static final String STATION_NAME = "station_name";

    // Activity parameters
    private String stationId;
    private String stationName;

    private Context context;
    private FragmentHeaderCallback headerCallback;

    // Activity UI elements
    RecyclerView rv;
    Adapter adapter;

    ProgressBar progressBar;
    LinearLayout errorContainer;
    TextView errorText;
    ImageView errorImageView;
    TextView tryAgainText;

    private TravanaApp app;
    private NetworkConnectivityManager networkConnectivityManager;

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
        initElements(root);

        app = TravanaApp.getInstance();
        networkConnectivityManager = app.getNetworkConnectivityManager();

        retrieveRoutes();

        return root;
    }

    private void retrieveRoutes() {

        if (!networkConnectivityManager.isConnectionAvailable()) {
            setupUi(ERROR);
            setErrorUi(this.getResources().getString(R.string.no_internet_connection), R.drawable.ic_no_wifi);
            return;
        }
        setupUi(LOADING);

        // Query all routes on station
        Api.routesOnStation(stationId, (apiResponse, statusCode, success) -> {
            Activity activity = getActivity();
            if (activity == null) return;
            if (success) {
                activity.runOnUiThread(() -> adapter.setRoutes(apiResponse.getData()));
                setupUi(DONE);
            } else {
                setupUi(ERROR);
                setErrorUi(this.getResources().getString(R.string.error_loading), R.drawable.ic_error_outline);
            }
        });

    }

    void setErrorUi(String errorName, int errorIconCode) {
        Activity activity = getActivity();
        if (activity == null) return;
        activity.runOnUiThread(() -> {
            errorText.setText(errorName);
            errorImageView.setImageResource(errorIconCode);
        });
    }

    void setupUi(ScreenState screenState) {
        Activity activity = getActivity();
        if (activity == null) return;
        activity.runOnUiThread(() -> {
            switch (screenState) {
                case DONE: {
                    this.progressBar.setVisibility(View.GONE);
                    this.rv.setVisibility(View.VISIBLE);
                    this.errorContainer.setVisibility(View.GONE);
                    break;
                }
                case LOADING: {
                    this.progressBar.setVisibility(View.VISIBLE);
                    this.rv.setVisibility(View.GONE);
                    this.errorContainer.setVisibility(View.GONE);
                    break;
                }
                case ERROR: {
                    this.progressBar.setVisibility(View.GONE);
                    this.rv.setVisibility(View.GONE);
                    this.errorContainer.setVisibility(View.VISIBLE);
                    break;
                }
            }
        });
    }

    private void initElements(View root) {
        rv = root.findViewById(R.id.routes_on_station_rv);
        progressBar = root.findViewById(R.id.progressBar);
        errorText = root.findViewById(R.id.tv_error);
        errorImageView = root.findViewById(R.id.iv_error);
        tryAgainText = root.findViewById(R.id.tv_try_again);
        errorContainer = root.findViewById(R.id.ll_error_container);

        // Setup UI
        adapter = new Adapter();
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                onHeaderChanged(rv.canScrollVertically(-1));
            }
        });

        tryAgainText.setOnClickListener(v -> {
            retrieveRoutes();
        });
    }

    private void onHeaderChanged(boolean value) {
        if (headerCallback != null)
            headerCallback.onHeaderChanged(value);
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        List<RouteOnStation> routes = new ArrayList<>();
        List<RouteOnStation> filteredRoutes = new ArrayList<>();    // routes without 'garage' routes

        public void setRoutes(List<RouteOnStation> routes) {
            this.routes = routes;

            for (RouteOnStation route : routes) {
                if (!isRouteInList(filteredRoutes, route.getTripId()) && !route.isGarage()) {
                    filteredRoutes.add(route);
                }
            }

            notifyDataSetChanged();
        }

        private boolean isRouteInList(List<RouteOnStation> routes, String trip_id) {
            for (RouteOnStation route : routes) {
                if (route.getTripId().equals(trip_id)) {
                    return true;
                }
            }
            return false;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(getLayoutInflater().inflate(R.layout.template_route, parent, false));
        }

        @Override
        public int getItemCount() {

            return filteredRoutes.size();
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            RouteOnStation route = filteredRoutes.get(position);

            // Set route name and number
            String name = route.getRouteGroupName();
            holder.name.setText(name);
            holder.number.setText(route.getRouteNumber());

            // Set route color
            holder.circle.getBackground().setTint(Colors.getColorFromString(route.getRouteNumber()));

            // Start DepartureActivity on click
            holder.departure.setOnClickListener(v -> {
                Intent intent = new Intent(context, DepartureActivity.class);
                intent.putExtra(DepartureActivity.ROUTE_NAME, route.getRouteGroupName());
                intent.putExtra(DepartureActivity.ROUTE_NUMBER, route.getRouteNumber());
                intent.putExtra(DepartureActivity.STATION_NAME, stationName);
                intent.putExtra(DepartureActivity.STATION_CODE, stationId);
                intent.putExtra(DepartureActivity.ROUTE_GARAGE, route.isGarage());
                startActivity(intent);
            });

            // Start RouteActivity on click
            holder.map.setOnClickListener(v -> {
                Intent intent = new Intent(context, RouteActivity.class);
                intent.putExtra(RouteActivity.ROUTE_NAME, route.getRouteGroupName());
                intent.putExtra(RouteActivity.ROUTE_NUMBER, route.getRouteNumber());
                intent.putExtra(RouteActivity.ROUTE_ID, route.getRouteId());
                intent.putExtra(RouteActivity.TRIP_ID, route.getTripId());
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
