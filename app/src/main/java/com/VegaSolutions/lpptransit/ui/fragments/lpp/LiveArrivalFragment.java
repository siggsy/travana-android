package com.VegaSolutions.lpptransit.ui.fragments.lpp;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.TravanaApp;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.ArrivalWrapper;
import com.VegaSolutions.lpptransit.ui.activities.RouteActivity;
import com.VegaSolutions.lpptransit.ui.fragments.FragmentHeaderCallback;
import com.VegaSolutions.lpptransit.utility.Colors;
import com.VegaSolutions.lpptransit.utility.LppHelper;
import com.VegaSolutions.lpptransit.utility.NetworkConnectivityManager;
import com.VegaSolutions.lpptransit.utility.ScreenState;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;
import com.google.android.flexbox.FlexboxLayout;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.VegaSolutions.lpptransit.utility.ScreenState.DONE;
import static com.VegaSolutions.lpptransit.utility.ScreenState.ERROR;
import static com.VegaSolutions.lpptransit.utility.ScreenState.LOADING;

public class LiveArrivalFragment extends Fragment {

    public static final String TAG = "LiveArrivalFragment";

    private static final String STATION_ID = "station_id";
    private static final int UPDATE_PERIOD = 10000;
    private static final int MAX_FAILED_CALLS_IN_ROW = 3;

    private String stationId;
    private Context context;
    private FragmentHeaderCallback headerCallback;

    private Adapter adapter;

    RecyclerView rv;
    LinearLayout noArrivalsContainer;
    ProgressBar progressBar;
    LinearLayout errorContainer;
    TextView errorText;
    ImageView errorImageView;
    TextView tryAgainText;

    private TravanaApp app;
    private NetworkConnectivityManager networkConnectivityManager;

    private ScreenState screenState = LOADING;

    private boolean isFirstCallRetrieveLiveArrivals = true;
    private int numberOfCallsFailedInRow = 0;

    private boolean hour;
    private int color, backColor;

    private Handler handler;

    private final Runnable updater = new Runnable() {
        @Override
        public void run() {
            retrieveLiveArrivals();
            handler.postDelayed(updater, UPDATE_PERIOD);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", MODE_PRIVATE);
        hour = sharedPreferences.getBoolean("hour", false);

        if (getArguments() != null) {
            stationId = getArguments().getString(STATION_ID);
        }

        app = TravanaApp.getInstance();
        networkConnectivityManager = app.getNetworkConnectivityManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_live_arrival, container, false);
        initElements(root);

        return root;

    }

    private void retrieveLiveArrivals() {

        handler.removeCallbacks(updater);
        handler.postDelayed(updater, UPDATE_PERIOD);

        if (!networkConnectivityManager.isConnectionAvailable()) {
            if (isFirstCallRetrieveLiveArrivals || numberOfCallsFailedInRow >= MAX_FAILED_CALLS_IN_ROW) {
                setupUi(ERROR);
                setErrorUi(this.getResources().getString(R.string.no_internet_connection), R.drawable.ic_no_wifi);
            } else {
                numberOfCallsFailedInRow++;
            }
            isFirstCallRetrieveLiveArrivals = false;
            return;
        }
        if (isFirstCallRetrieveLiveArrivals || screenState == ERROR) {
            setupUi(LOADING);
        }

        Api.arrival(stationId, (apiResponse, statusCode, success) -> {

            if (context == null) {
                return;
            }

            if (success) {
                numberOfCallsFailedInRow = 0;
                ArrivalWrapper arrivalWrapper = apiResponse.getData();
                ((Activity) context).runOnUiThread(() -> {
                    noArrivalsContainer.setVisibility(arrivalWrapper.getArrivals().isEmpty() ? View.VISIBLE : View.GONE);
                    adapter.setArrivals(RouteWrapper.getFromArrivals(context, arrivalWrapper.getArrivals()));
                });
                setupUi(DONE);
            } else {
                numberOfCallsFailedInRow++;
                if (isFirstCallRetrieveLiveArrivals || numberOfCallsFailedInRow >= 5) {
                    setupUi(ERROR);
                    setErrorUi(this.getResources().getString(R.string.error_loading), R.drawable.ic_error_outline);
                }
            }

        });
        isFirstCallRetrieveLiveArrivals = false;
    }

    void setErrorUi(String errorName, int errorIconCode) {
        ((Activity) context).runOnUiThread(() -> {
            errorText.setText(errorName);
            errorImageView.setImageResource(errorIconCode);
        });
    }

    void setupUi(ScreenState screenState) {
        this.screenState = screenState;
        ((Activity) context).runOnUiThread(() -> {
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
                    this.noArrivalsContainer.setVisibility(View.GONE);
                    this.errorContainer.setVisibility(View.GONE);
                    break;
                }
                case ERROR: {
                    this.progressBar.setVisibility(View.GONE);
                    this.rv.setVisibility(View.GONE);
                    this.noArrivalsContainer.setVisibility(View.GONE);
                    this.errorContainer.setVisibility(View.VISIBLE);
                    break;
                }
            }
        });
    }

    private void initElements(View root) {
        rv = root.findViewById(R.id.live_arrival_rv);
        noArrivalsContainer = root.findViewById(R.id.live_arrival_no_arrivals_error);
        progressBar = root.findViewById(R.id.progress_bar);
        errorText = root.findViewById(R.id.tv_error);
        errorImageView = root.findViewById(R.id.iv_error);
        tryAgainText = root.findViewById(R.id.tv_try_again);
        errorContainer = root.findViewById(R.id.ll_error_container);
        rv = root.findViewById(R.id.live_arrival_rv);
        noArrivalsContainer = root.findViewById(R.id.live_arrival_no_arrivals_error);

        tryAgainText.setOnClickListener(v -> {
            retrieveLiveArrivals();
        });

        // Save default theme colors
        int[] attribute = new int[]{android.R.attr.textColor, R.attr.backgroundElevatedColor};
        TypedArray array = context.obtainStyledAttributes(ViewGroupUtils.isDarkTheme(context) ? R.style.DarkTheme : R.style.WhiteTheme, attribute);
        backColor = array.getColor(1, Color.WHITE);
        color = array.getColor(0, Color.BLACK);
        array.recycle();

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

        handler = new Handler(Looper.myLooper());
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
        retrieveLiveArrivals();
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(updater);
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
            viewHolder.number.setText(route.arrivalObject.getRouteName());
            viewHolder.circle.getBackground().setTint(Colors.getColorFromString(route.arrivalObject.getRouteName()));
            viewHolder.favourite.setImageDrawable(ContextCompat.getDrawable(getContext(), route.favourite ? R.drawable.ic_baseline_push_pin : R.drawable.ic_outline_push_pin));
            viewHolder.route.setOnClickListener(v -> {
                Intent i = new Intent(context, RouteActivity.class);
                i.putExtra(RouteActivity.ROUTE_NAME, route.arrivalObject.getTripName());
                i.putExtra(RouteActivity.ROUTE_NUMBER, route.arrivalObject.getRouteName());
                i.putExtra(RouteActivity.ROUTE_ID, route.arrivalObject.getRouteId());
                i.putExtra(RouteActivity.TRIP_ID, route.arrivalObject.getTripId());
                i.putExtra(RouteActivity.STATION_ID, stationId);
                startActivity(i);
            });

            viewHolder.favourite.setOnClickListener(v1 -> {
                SharedPreferences sharedPreferences = context.getSharedPreferences(LppHelper.ROUTE_FAVOURITES, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(route.arrivalObject.getTripId(), !route.favourite);
                route.favourite = !route.favourite;

                viewHolder.favourite.setImageDrawable(ContextCompat.getDrawable(getContext(), route.favourite ? R.drawable.ic_baseline_push_pin : R.drawable.ic_outline_push_pin));

                // sort routes in the recyclerview and animate them

                // find index of clicked route in the routes
                // NOTE: var position can not be used,because it is not updated when calling function notifyItemMoved()
                int routeIndex = 0;
                for (int i = 0; i < routes.size(); i++) {
                    if (routes.get(i).equals(route)) {
                        routeIndex = i;
                    }
                }

                // calculate new place for clicked route
                int to = 0;
                for (int i = 0; i < routes.size(); i++) {
                    RouteWrapper route2 = routes.get(i);
                    if (i == routeIndex) continue;
                    if (route.favourite) {
                        if (!route2.favourite) {
                            continue;
                        }
                        if (route2.getSortingRouteNumber() < route.getSortingRouteNumber()) {
                            to++;
                        } else {
                            break;
                        }
                    } else {
                        if (route2.favourite) {
                            to++;
                            continue;
                        }
                        if (route2.getSortingRouteNumber() < route.getSortingRouteNumber()) {
                            to++;
                        } else {
                            break;
                        }
                    }
                }

                routes.remove(routeIndex);
                routes.add(to, route);
                notifyItemMoved(routeIndex, to);

                editor.apply();
            });

            // Set live arrivals.
            viewHolder.arrivals.removeAllViews();
            for (ArrivalWrapper.Arrival arrival : route.arrivals) {

                // Inflate view
                View v = getLayoutInflater().inflate(R.layout.template_arrival_time, viewHolder.arrivals, false);
                TextView arrival_time = v.findViewById(R.id.arrival_time_time);
                TextView arrival_event = v.findViewById(R.id.arrival_time_event);
                View arrival_event_icon = v.findViewById(R.id.arrival_time_event_rss);
                View back = v.findViewById(R.id.arrival_time_back);

                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());

                // Set preferred time format
                arrival_time.setText(hour ? formatter.format(DateTime.now().plusMinutes(arrival.getEtaMin()).toDate()) : String.format("%s min", arrival.getEtaMin()));
                arrival_time.setTextColor(color);
                back.getBackground().setTint(backColor);

                // (0 - predicted, 1 - scheduled, 2 - approaching station (prihod), 3 - detour (obvoz))
                switch (arrival.getType()) {
                    case 0:
                        arrival_event_icon.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        arrival_event.setVisibility(View.VISIBLE);
                        arrival_event_icon.setVisibility(View.GONE);
                        arrival_time.setText(getString(R.string.arrival).toUpperCase());
                        arrival_time.setTextColor(Color.WHITE);
                        break;
                    case 3:
                        arrival_event.setVisibility(View.GONE);
                        arrival_event_icon.setVisibility(View.GONE);
                        arrival_time.setText(getString(R.string.detour).toUpperCase());
                        arrival_time.setTextColor(Color.WHITE);
                        break;
                    default:
                        arrival_event.setVisibility(View.GONE);
                        arrival_event_icon.setVisibility(View.GONE);
                }

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

                // Ignore "ghost" arrivals
                if (!arrival.getVehicleId().equals("22222222-2222-2222-2222-222222222222"))
                    viewHolder.arrivals.addView(v);

            }

        }

        @Override
        public int getItemCount() {
            return routes.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            TextView name, number;
            View circle;
            ImageView favourite;

            LinearLayout route;
            FlexboxLayout arrivals;

            private ViewHolder(@NonNull View itemView) {
                super(itemView);

                name = itemView.findViewById(R.id.live_arrival_route_name);
                number = itemView.findViewById(R.id.route_station_number);
                circle = itemView.findViewById(R.id.route_station_circle);
                arrivals = itemView.findViewById(R.id.live_arrival_arrivals);
                route = itemView.findViewById(R.id.live_arrival_ll);
                favourite = itemView.findViewById(R.id.route_favourite);

            }
        }
    }

    private static class RouteWrapper {

        List<ArrivalWrapper.Arrival> arrivals = new ArrayList<>();
        String name;
        ArrivalWrapper.Arrival arrivalObject;
        boolean favourite;

        private String getRouteName() {
            if (arrivals == null || arrivals.size() == 0) {
                return "";
            } else {
                return arrivals.get(0).getRouteName();
            }
        }

        private int getRouteNumber() {
            return Integer.parseInt(getRouteName().replaceAll("[^\\d.]", ""));
        }

        // returns number based on route name
        // used for sorting routes by name
        // eg. route 3 -> 300066 (300000 + ascii(B))
        //     route 3g -> 30103 (300000 + ascii(g) * 1000 + ascii(B))
        //ascii(B) - first letter of the destination station
        private int getSortingRouteNumber() {
            int value = getRouteNumber() * 1000000;
            String routeNameLetters = getRouteName().replaceAll("\\d", "");
            if (routeNameLetters.length() > 0) {
                value += getRouteName().replaceAll("\\d", "").charAt(0) * 1000;
            }
            if (name.length() > 0) {
                value += name.charAt(0);
            }
            return value;
        }

        private static List<RouteWrapper> getFromArrivals(Context context, List<ArrivalWrapper.Arrival> arrivals) {

            if (context == null)
                return new ArrayList<>();

            // Sort by route number
            Collections.sort(arrivals, (o1, o2) -> {
                String o1S = o1.getRouteName().replaceAll("[^0-9]", "");
                String o2S = o2.getRouteName().replaceAll("[^0-9]", "");
                int o1V = Integer.parseInt(o1S);
                int o2V = Integer.parseInt(o2S);
                if (o1V == o2V) return o1.getRouteName().compareTo(o2.getRouteName());
                return Integer.compare(o1V, o2V);
            });

            Map<String, Boolean> fav = LppHelper.getFavouriteRoutes(context);
            int k = 0;
            for (int i = 0; i < arrivals.size(); i++) {
                ArrivalWrapper.Arrival arrival = arrivals.get(i);
                Boolean f = fav.get(arrival.getTripId());
                if (f != null && f) {
                    arrivals.remove(i);
                    arrivals.add(k, arrival);
                    k++;
                }
            }

            Map<String, RouteWrapper> map = new LinkedHashMap<>();
            for (ArrivalWrapper.Arrival arrival : arrivals) {
                RouteWrapper route = map.get(arrival.getTripId());
                if (route == null) {
                    route = new RouteWrapper();
                    route.favourite = context.getSharedPreferences(LppHelper.ROUTE_FAVOURITES, MODE_PRIVATE).getBoolean(arrival.getTripId(), false);
                    ArrivalWrapper.Arrival.Stations stations = arrival.getStations();
                    route.name = stations != null && stations.getArrival() != null && !stations.getArrival().equals("") ? stations.getArrival() : arrival.getTripName();
                    route.arrivalObject = arrival;
                    map.put(arrival.getTripId(), route);
                }
                route.arrivals.add(arrival);
            }

            return new ArrayList<>(map.values());

        }

    }

}
