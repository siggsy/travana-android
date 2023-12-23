package com.VegaSolutions.lpptransit.ui.fragments.lpp.subfragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.TravanaApp;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Station;
import com.VegaSolutions.lpptransit.ui.activities.MainActivity;
import com.VegaSolutions.lpptransit.ui.activities.SearchActivity;
import com.VegaSolutions.lpptransit.ui.activities.StationActivity;
import com.VegaSolutions.lpptransit.ui.custommaps.TravanaLocationManager;
import com.VegaSolutions.lpptransit.ui.customviews.NullSafeView;
import com.VegaSolutions.lpptransit.ui.fragments.FragmentHeaderCallback;
import com.VegaSolutions.lpptransit.utility.Colors;
import com.VegaSolutions.lpptransit.utility.Constants;
import com.VegaSolutions.lpptransit.utility.LppHelper;
import com.VegaSolutions.lpptransit.utility.MapUtility;
import com.VegaSolutions.lpptransit.utility.NetworkConnectivityManager;
import com.VegaSolutions.lpptransit.utility.ScreenState;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StationsSubFragment extends Fragment implements TravanaLocationManager.TravanaLocationListener {

    private static final String TYPE = "type";

    public static final int TYPE_NEARBY = 1;
    public static final int TYPE_FAVOURITE = 2;

    // Fragment parameters
    private int type;

    // Probably redundant
    private LatLng location;

    private Context context;
    private TravanaLocationManager locationManager;

    // UI elements
    private final NullSafeView<RecyclerView> list = new NullSafeView<>();
    public final NullSafeView<View> noFavoritesView = new NullSafeView<>();
    private final NullSafeView<View> noLocationEnabledView = new NullSafeView<>();
    private final NullSafeView<View> progressBar = new NullSafeView<>();
    private final Adapter adapter = new Adapter();
    private FragmentHeaderCallback callback;
    private OnAttachListener onAttachListener = null;

    TextView enableLocation;
    TextView addFavorites;
    LinearLayout errorContainer;
    TextView errorText;
    ImageView errorImageView;
    TextView tryAgainText;


    MainActivity mainActivity;
    TravanaApp app;
    NetworkConnectivityManager networkConnectivityManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_stations_sub, container, false);

        initElements(root);
        setupUi(ScreenState.DONE);

        app = TravanaApp.getInstance();
        networkConnectivityManager = app.getNetworkConnectivityManager();
        mainActivity = (MainActivity) getActivity();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        callback.onHeaderChanged(list.getView().canScrollVertically(-1));
        updateStations();
    }


    private void initElements(View root) {
        // Find all UI elements
        list.setView(root.findViewById(R.id.stations_sub_list));
        noLocationEnabledView.setView(root.findViewById(R.id.stations_sub_list_nearby_error));
        noFavoritesView.setView(root.findViewById(R.id.stations_sub_list_favourite_error));
        progressBar.setView(root.findViewById(R.id.stations_sub_progress));

        enableLocation = root.findViewById(R.id.tv_enable_location);
        addFavorites = root.findViewById(R.id.tv_add_favorites);
        errorText = root.findViewById(R.id.tv_error);
        errorImageView = root.findViewById(R.id.iv_error);
        tryAgainText = root.findViewById(R.id.tv_try_again);
        errorContainer = root.findViewById(R.id.ll_error_container);

        enableLocation.setOnClickListener(view -> {
            if (!MapUtility.checkIfAtLeastOnePermissionPermitted(getContext()))
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.LOCATION_REQUEST_CODE);
        });
        addFavorites.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), SearchActivity.class);
            startActivity(i);
        });

        // RV
        list.getView().setAdapter(adapter);
        list.getView().setLayoutManager(new LinearLayoutManager(context));
        list.getView().setItemViewCacheSize(30);
        list.getView().setHasFixedSize(true);
        list.getView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                callback.onHeaderChanged(recyclerView.canScrollVertically(-1));
            }
        });

        tryAgainText.setOnClickListener(v -> {
            mainActivity.retrieveStations();
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

    public void setupUi(ScreenState screenState) {
        Activity activity = getActivity();
        if (activity == null) return;
        activity.runOnUiThread(() -> {
            switch (screenState) {
                case DONE: {
                    this.progressBar.addTask(view -> {
                        view.setVisibility(View.GONE);
                    });
                    this.list.addTask(view -> {
                        view.setVisibility(View.VISIBLE);
                    });
                    this.errorContainer.setVisibility(View.GONE);
                    break;
                }
                case LOADING: {
                    this.progressBar.addTask(view -> {
                        view.setVisibility(View.VISIBLE);
                    });
                    this.list.addTask(view -> {
                        view.setVisibility(View.GONE);
                    });
                    this.errorContainer.setVisibility(View.GONE);
                    break;
                }
                case ERROR: {
                    this.progressBar.addTask(view -> {
                        view.setVisibility(View.GONE);
                    });
                    this.list.addTask(view -> {
                        view.setVisibility(View.GONE);
                    });
                    this.errorContainer.setVisibility(View.VISIBLE);
                    break;
                }
            }
        });
    }

    public void updateStations() {
        if (mainActivity == null) {
            return;
        }

        if (mainActivity.screenState == ScreenState.DONE && type == TYPE_NEARBY) {
            setStations();
            return;
        }

        setupUi(mainActivity.screenState);
        if (mainActivity.screenState == ScreenState.ERROR) {
            switch (mainActivity.errorCode) {
                case NetworkConnectivityManager.NO_INTERNET_CONNECTION: {
                    setErrorUi(this.getResources().getString(R.string.no_internet_connection), R.drawable.ic_no_wifi);
                    break;
                }
                case NetworkConnectivityManager.ERROR_DURING_LOADING: {
                    setErrorUi(this.getResources().getString(R.string.error_loading), R.drawable.ic_error_outline);
                    break;
                }
            }
        } else if (mainActivity.screenState == ScreenState.DONE) {
            setStations();
        }
    }

    public void setStations() {

        // Update UI
        Activity activity = getActivity();
        if (activity == null) return;

        activity.runOnUiThread(() -> {
            // Show favourite stations
            if (type == TYPE_FAVOURITE) {
                setFavouriteStations(app.getStations());
            }

            // Show nearby stations
            else if (type == TYPE_NEARBY) {
                // Check permissions
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!MapUtility.checkIfAtLeastOnePermissionPermitted(context)) {
                        noLocationEnabledView.addTask(v -> v.setVisibility(View.VISIBLE));
                        return;
                    }
                }

                // Setup location for updates
                locationManager = TravanaApp.getInstance().getLocationManager();
                if (!locationManager.isMainProviderEnabled()) {
                    locationManager.addListener(this);
                }
                noLocationEnabledView.addTask(v -> v.setVisibility(View.GONE));
                updateLocationList(locationManager.getLatest());
            }
        });

    }


    private void setFavouriteStations(List<Station> stations) {

        Map<String, Boolean> favourites = LppHelper.getFavouriteStations(context);
        ArrayList<StationWrapper> stationWrappersFav = new ArrayList<>();
        for (Station station : stations) {
            Boolean f = favourites.get(station.getRefId());
            if (f == null) f = false;
            if (f) stationWrappersFav.add(new StationWrapper(station, true));
        }

        // Show error if favourites list is empty
        if (stationWrappersFav.size() == 0) {
            noFavoritesView.addTask(v -> v.setVisibility(View.VISIBLE));
        } else {
            noFavoritesView.addTask(v -> v.setVisibility(View.GONE));
        }
        adapter.submitList(stationWrappersFav);
        list.getView().scrollToPosition(0);

    }

    private void setNearbyStations(List<Station> stations) {
        Map<String, Boolean> favourites = LppHelper.getFavouriteStations(context);

        ArrayList<StationWrapper> stationWrappersFav = new ArrayList<>();

        // Check for location availability
        if (location == null) {
            adapter.submitList(new ArrayList<>());
            noFavoritesView.addTask(v -> v.setVisibility(View.GONE));
            noLocationEnabledView.addTask(v -> v.setVisibility(View.VISIBLE));
        } else {
            setupUi(ScreenState.LOADING);
            app.getThreadPool().execute(() -> {
                // Add "favourite" flag and calculate distance
                for (Station station : stations) {
                    Boolean f = favourites.get(station.getRefId());
                    if (f == null) f = false;
                    stationWrappersFav.add(new StationWrapper(station, f, (int) Math.round(MapUtility.calculationByDistance(location, station.getLatLng()) * 1000)));
                }

                // Sort stations by current location
                Collections.sort(stationWrappersFav, (o1, o2) -> Double.compare(o1.distance, o2.distance));

                // Show on recyclerView
                mainActivity.runOnUiThread(() -> {
                    noFavoritesView.addTask(v -> v.setVisibility(View.GONE));
                    noLocationEnabledView.addTask(v -> v.setVisibility(View.GONE));
                });
                adapter.submitList(stationWrappersFav.subList(0, 30), () -> { setupUi(ScreenState.DONE); });
            });
        }

    }

    private void updateLocationList(LatLng location) {
        Activity activity = getActivity();
        if (activity == null) return;
        activity.runOnUiThread(() -> {
            if (location != null) {
                if (location.equals(this.location)) return;
                this.location = location;
                setNearbyStations(app.getStations());
                noLocationEnabledView.addTask(v -> v.setVisibility(View.GONE));
            } else {
                noLocationEnabledView.addTask(v -> v.setVisibility(View.VISIBLE));
            }
        });
    }

    public static StationsSubFragment newInstance(int type, FragmentHeaderCallback callback) {
        StationsSubFragment fragment = new StationsSubFragment();
        fragment.callback = callback;
        Bundle args = new Bundle();
        args.putInt(TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt(TYPE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (type == TYPE_NEARBY && locationManager != null)
            locationManager.removeListener(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if (onAttachListener != null) {
            onAttachListener.onAttach();
            onAttachListener = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
    }

    @Override
    public void onLocationChanged(Location location) {
        updateLocationList(MapUtility.getLatLngFromLocation(location));
    }

    @Override
    public void onProviderAvailabilityChanged(boolean value) {
        // Nothing
    }

    private static final DiffUtil.ItemCallback<StationWrapper> DIFF_CALLBACK = new DiffUtil.ItemCallback<StationWrapper>() {
        @Override
        public boolean areItemsTheSame(@NonNull StationWrapper oldItem, @NonNull StationWrapper newItem) {
            return oldItem.station.getRefId().equals(newItem.station.getRefId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull StationWrapper oldItem, @NonNull StationWrapper newItem) {
            return oldItem.distance == newItem.distance;
        }
    };

    public class Adapter extends ListAdapter<StationWrapper, RecyclerView.ViewHolder> {

        private Adapter() {
            super(DIFF_CALLBACK);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(getLayoutInflater().inflate(R.layout.template_station, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            StationWrapper station = getItem(position);
            ViewHolder viewHolder = (ViewHolder) holder;

            // Set distance
            String distance;
            if (type != TYPE_NEARBY) distance = "";
            else {
                if (station.distance < 900) distance = station.distance + " m";
                else distance = String.format(Locale.getDefault(), "%.2f km", station.distance / 1000f);
            }

            // Update ViewHolder
            viewHolder.name.setText(station.station.getName());
            viewHolder.distance.setText(distance);
            viewHolder.center.setVisibility(station.station.isCenter()? View.VISIBLE : View.GONE);
            viewHolder.fav.setVisibility(station.favourite ? View.VISIBLE : View.GONE);
            viewHolder.card.setOnClickListener(v -> {
                Intent intent = new Intent(context, StationActivity.class);
                intent.putExtra("station", station.station);
                startActivity(intent);
            });

            // Set all route circles
            viewHolder.routes.removeAllViews();
            for (String route : station.station.getRouteGroupsOnStation()) {
                View v = getLayoutInflater().inflate(R.layout.template_route_number, viewHolder.routes, false);
                ((TextView) v.findViewById(R.id.route_station_number)).setText(route);

                // Set route circle color
                v.findViewById(R.id.route_station_circle).getBackground().setTint(Colors.getColorFromString(route));
                viewHolder.routes.addView(v);
            }

        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            TextView name, distance, center;
            FlexboxLayout routes;
            ImageView fav;

            LinearLayout card;

            ViewHolder(@NonNull View itemView) {
                super(itemView);

                name = itemView.findViewById(R.id.station_nearby_name);
                distance = itemView.findViewById(R.id.station_nearby_distance);
                routes = itemView.findViewById(R.id.station_nearby_ll);
                card = itemView.findViewById(R.id.station_nearby_card);
                center = itemView.findViewById(R.id.station_nearby_center);
                fav = itemView.findViewById(R.id.route_favourite);

            }
        }
    }

    private static class StationWrapper {

        int distance;
        boolean favourite;
        Station station;

        private StationWrapper(Station station, boolean favourite) {
            this(station, favourite, -1);
        }

        private StationWrapper(Station station, boolean favourite, int distance) {
            this.distance = distance;
            this.favourite = favourite;
            this.station = station;
        }

    }

    interface OnAttachListener {
        void onAttach();
    }
}
