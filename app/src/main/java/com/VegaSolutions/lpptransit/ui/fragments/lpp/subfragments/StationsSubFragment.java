package com.VegaSolutions.lpptransit.ui.fragments.lpp.subfragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Station;
import com.VegaSolutions.lpptransit.ui.activities.SearchActivity;
import com.VegaSolutions.lpptransit.ui.activities.lpp.StationActivity;
import com.VegaSolutions.lpptransit.ui.custommaps.MyLocationManager;
import com.VegaSolutions.lpptransit.ui.customviews.NullSafeView;
import com.VegaSolutions.lpptransit.ui.fragments.FragmentHeaderCallback;
import com.VegaSolutions.lpptransit.utility.Colors;
import com.VegaSolutions.lpptransit.utility.Constants;
import com.VegaSolutions.lpptransit.utility.LppHelper;
import com.VegaSolutions.lpptransit.utility.MapUtility;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StationsSubFragment extends Fragment implements MyLocationManager.MyLocationListener {

    private static final String TYPE = "type";

    public static final int TYPE_NEARBY = 1;
    public static final int TYPE_FAVOURITE = 2;

    // Fragment parameters
    private int type;

    // Probably redundant
    private LatLng location;


    private Context context;
    // private FusedLocationProviderClient locationClient;
    private MyLocationManager locationManager;
    private List<Station> stations;

    // UI elements
    private final NullSafeView<RecyclerView> list = new NullSafeView<>();
    private final NullSafeView<View> favErr = new NullSafeView<>();
    private final NullSafeView<View> locErr = new NullSafeView<>();
    private final NullSafeView<View> progressBar = new NullSafeView<>();
    private final Adapter adapter = new Adapter();
    private FragmentHeaderCallback callback;

    private OnAttachListener onAttachListener = null;

    private void updateLocationList(LatLng location) {

        // Cancel UI update if fragment not attached
        if (context == null)
            return;

        // Update UI
        ((Activity) context).runOnUiThread(() -> {
            progressBar.addTask(v -> v.setVisibility(View.GONE));
            if (location != null) {
                this.location = location;
                setNearbyStations(stations);
                locErr.addTask(v -> v.setVisibility(View.GONE));
            } else locErr.addTask(v -> v.setVisibility(View.VISIBLE));
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

    private void setupUI() {

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

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt(TYPE);
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        callback.onHeaderChanged(list.getView().canScrollVertically(-1));
        if (stations != null) {
            if (type == TYPE_FAVOURITE) { adapter.notifyDataSetChanged(); setFavouriteStations(stations); }
            else if (locationManager != null) updateLocationList(locationManager.getLatest());
        }
        if (type == TYPE_NEARBY && locationManager != null && !locationManager.isMainProviderEnabled()) {
            if (stations != null)
                locationManager.addListener(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (type == TYPE_NEARBY && locationManager != null)
            locationManager.removeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_stations_sub, container, false);

        // Find all UI elements
        list.setView(root.findViewById(R.id.stations_sub_list));
        locErr.setView(root.findViewById(R.id.stations_sub_list_nearby_error));
        favErr.setView(root.findViewById(R.id.stations_sub_list_favourite_error));
        progressBar.setView(root.findViewById(R.id.stations_sub_progress));

        TextView enableLocation = root.findViewById(R.id.tv_enable_location);
        enableLocation.setOnClickListener(view -> {
            // Check for permission.
            if (!MapUtility.checkIfAtLeastOnePermissionPermitted(getContext()))
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.LOCATION_REQUEST_CODE);
        });
        TextView addFavorites = root.findViewById(R.id.tv_add_favorites);
        addFavorites.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), SearchActivity.class);
            startActivity(i);
        });

        setupUI();

        return root;
    }

    private void setFavouriteStations(List<Station> stations) {

        Map<String, Boolean> favourites = LppHelper.getFavouriteStations(context);

        ArrayList<StationWrapper> stationWrappersFav = new ArrayList<>();

        for (Station station : stations) {
            Boolean f = favourites.get(station.getRef_id());
            if (f == null) f = false;
            if (f) stationWrappersFav.add(new StationWrapper(station, true));
        }

        // Show error if favourites list is empty
        if (stationWrappersFav.size() == 0) {
            locErr.addTask(v -> v.setVisibility(View.GONE));
            favErr.addTask(v -> v.setVisibility(View.VISIBLE));
        } else {
            favErr.addTask(v -> v.setVisibility(View.GONE));
            locErr.addTask(v -> v.setVisibility(View.GONE));
        }
        adapter.setStations(stationWrappersFav);

    }

    private void setNearbyStations(List<Station> stations) {
        Map<String, Boolean> favourites = LppHelper.getFavouriteStations(context);

        ArrayList<StationWrapper> stationWrappersFav = new ArrayList<>();

        // Check for location availability
        if (location == null) {
            adapter.setStations(new ArrayList<>());
            favErr.addTask(v -> v.setVisibility(View.GONE));
            locErr.addTask(v -> v.setVisibility(View.VISIBLE));
        } else {


            // Add "favourite" flag and calculate distance
            for (Station station : stations) {
                Boolean f = favourites.get(station.getRef_id());
                if (f == null) f = false;
                stationWrappersFav.add(new StationWrapper(station, f, (int) Math.round(MapUtility.calculationByDistance(location, station.getLatLng()) * 1000)));
            }

            // Sort stations by current location
            Collections.sort(stationWrappersFav, (o1, o2) -> Double.compare(o1.distance, o2.distance));

            // Show on recyclerView
            favErr.addTask(v -> v.setVisibility(View.GONE));
            locErr.addTask(v -> v.setVisibility(View.GONE));
            adapter.setStations(stationWrappersFav.subList(0, 30));
            adapter.refreshStations(stationWrappersFav.subList(0, 30));

        }

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

    public void setStations(List<Station> apiResponse, int statusCode, boolean success) {

        // Cancel UI update if fragment not attached
        if (context == null) {
            onAttachListener = () -> setStations(apiResponse, statusCode, success);
            return;
        }

        // Update UI
        ((Activity) context).runOnUiThread(() -> {
            progressBar.addTask(v -> v.setVisibility(View.GONE));
            if (success && apiResponse != null) {

                stations = apiResponse;

                // Show favourite stations
                if (type == TYPE_FAVOURITE) {
                    setFavouriteStations(apiResponse);
                    progressBar.addTask(v -> v.setVisibility(View.GONE));
                }

                // Show nearby stations
                else if (type == TYPE_NEARBY) {

                    // Check permissions
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!MapUtility.checkIfAtLeastOnePermissionPermitted(context)) {
                            locErr.addTask(v -> v.setVisibility(View.VISIBLE));
                            progressBar.addTask(v -> v.setVisibility(View.GONE));
                            return;
                        }
                    }

                    // Setup location for updates
                    locationManager = new MyLocationManager(context);
                    if (!locationManager.isMainProviderEnabled())
                        locationManager.addListener(this);
                    locErr.addTask(v -> v.setVisibility(View.GONE));
                    updateLocationList(locationManager.getLatest());

                }
            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
    }

    @Override
    public void onLocationChanged(Location location) {
        // Nothing
    }

    @Override
    public void onProviderAvailabilityChanged(boolean value) {
        // Nothing
    }

    public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public List<StationWrapper> stations;

        private Adapter() {
            stations = new ArrayList<>();
        }

        private void setStations(List<StationWrapper> stationsNew) {

            // Check for new elements
            for (int i = 0; i < stationsNew.size(); i++) {
                StationWrapper newStation = stationsNew.get(i);
                int j = where(newStation, stations);
                if (j == -1) {
                    // Add if not inserted
                    stations.add(i, newStation);
                    notifyItemInserted(i);
                } else {
                    StationWrapper oldStation = stations.get(j);
                    if (newStation.distance != oldStation.distance || newStation.favourite != oldStation.favourite) {
                        stations.remove(i);
                        stations.add(i, newStation);
                        notifyItemChanged(i);
                    }
                }
            }



            for (int i = 0; i < stations.size(); i++) {
                if (where(stations.get(i), stationsNew) == -1) {
                    stations.remove(i);
                    notifyItemRemoved(i);
                    i--;
                }
            }

        }

        private int where(StationWrapper a, List<StationWrapper> b) {
            if (b.isEmpty())
                return -1;
            for (int i = 0; i < b.size(); i++) {
                if (a.station.equals(b.get(i).station))
                    return i;
            }
            return -1;
        }

        private void refreshStations(List<StationWrapper> stationsNew) {

            // Check for new elements
            for (int i = 0; i < stationsNew.size(); i++) {
                int j = where(stationsNew.get(i), stations);

                if (j != -1) {
                    // Move if already in
                    if (i != j) {
                        stations.remove(j);
                        stations.add(i, stationsNew.get(i));
                        notifyItemMoved(j, i);
                    }
                } else {
                    // Insert if not
                    stations.add(i, stationsNew.get(i));
                    notifyItemInserted(i);
                }
            }

            for (int i = 0; i < stations.size(); i++) {
                if (where(stations.get(i), stationsNew) == -1) {
                    stations.remove(i);
                    notifyItemRemoved(i);
                    i--;
                }
            }
            list.getView().scrollToPosition(0);
        }

        public ArrayList<Station> getStations() {
            // Convert array
            ArrayList<Station> stations = new ArrayList<>();
            for (StationWrapper stationWrapper : this.stations)
                stations.add(stationWrapper.station);
            return stations;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(getLayoutInflater().inflate(R.layout.template_station_nearby, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            StationWrapper station = stations.get(position);
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
            for (String route : station.station.getRoute_groups_on_station()) {
                View v = getLayoutInflater().inflate(R.layout.template_route_number, viewHolder.routes, false);
                ((TextView) v.findViewById(R.id.route_station_number)).setText(route);

                // Set route circle color
                v.findViewById(R.id.route_station_circle).getBackground().setTint(Colors.getColorFromString(route));
                viewHolder.routes.addView(v);
            }


        }

        @Override
        public int getItemCount() {
            return stations.size();
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

    private class StationWrapper {

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
