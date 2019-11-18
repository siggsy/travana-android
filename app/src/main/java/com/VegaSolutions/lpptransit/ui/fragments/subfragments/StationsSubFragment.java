package com.VegaSolutions.lpptransit.ui.fragments.subfragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Station;
import com.VegaSolutions.lpptransit.ui.Colors;
import com.VegaSolutions.lpptransit.ui.activities.StationActivity;
import com.VegaSolutions.lpptransit.ui.fragments.FragmentHeaderCallback;
import com.VegaSolutions.lpptransit.utility.MapUtility;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.security.auth.callback.Callback;

import static android.content.Context.MODE_PRIVATE;

// TODO: Clean code and redo api call.

public class StationsSubFragment extends Fragment {

    private static final String TYPE = "type";

    public static final int TYPE_NEARBY = 1;
    public static final int TYPE_FAVOURITE = 2;

    private int minTime = 10000;
    private float minDistance = 15;
    private String bestProvider;

    private boolean resumed = false;

    private int type;

    private Location location;
    private Adapter adapter = new Adapter();

    private Context context;
    private FragmentHeaderCallback callback;
    private FusedLocationProviderClient locationClient;
    private List<Station> stations;
    private boolean firstTime = true;

    private RecyclerView list;
    private View fav_err, loc_err, progressBar;
    private FloatingActionButton locationRefresh;
    View root;

    private OnCompleteListener<Location> onCompleteListener = task -> {
        Location location = task.getResult();
        progressBar.setVisibility(View.GONE);
        if (location != null) {
            this.location = location;
            setNearbyStations(stations);
            loc_err.setVisibility(View.GONE);
        } else
            loc_err.setVisibility(View.VISIBLE);

    };


    public static StationsSubFragment newInstance(int type, FragmentHeaderCallback callback) {
        StationsSubFragment fragment = new StationsSubFragment();
        fragment.callback = callback;
        Bundle args = new Bundle();
        args.putInt(TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    private void setupUI() {
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(context));
        list.setItemViewCacheSize(30);
        list.setHasFixedSize(true);
        list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                callback.onHeaderChanged(recyclerView.canScrollVertically(-1));
            }
        });

        locationRefresh.setVisibility(View.GONE);
        if (type == TYPE_NEARBY) {
            locationRefresh.setOnClickListener((v) -> {
                loc_err.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                adapter.setStations(new ArrayList<>());
                locationClient.getLastLocation().addOnCompleteListener(onCompleteListener);
            });
        }
    }

    private void setupCurrentLocationUpdates() {
        locationClient = LocationServices.getFusedLocationProviderClient(context);
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
        callback.onHeaderChanged(list.canScrollVertically(-1));
        if (stations != null) {
            if (type == TYPE_FAVOURITE) setFavouriteStations(stations);
            else adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_stations_sub, container, false);
        list = root.findViewById(R.id.stations_sub_list);
        loc_err = root.findViewById(R.id.stations_sub_list_nearby_error);
        fav_err = root.findViewById(R.id.stations_sub_list_favourite_error);
        progressBar = root.findViewById(R.id.stations_sub_progress);
        locationRefresh = root.findViewById(R.id.location_refresh_fab);

        setupUI();

        Api.stationDetails(false, (apiResponse, statusCode, success) -> {
            if (context == null)
                return;
            ((Activity)context).runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                if (success) {
                    stations = apiResponse.getData();
                    // Get favourites
                    if (type == TYPE_FAVOURITE) {

                        setFavouriteStations(apiResponse.getData());
                        ((Activity)context).runOnUiThread(() -> progressBar.setVisibility(View.GONE));

                    } else if (type == TYPE_NEARBY) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                ((Activity)context).runOnUiThread(() -> {
                                    loc_err.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                });
                                return;
                            }
                        }
                        setupCurrentLocationUpdates();
                        loc_err.setVisibility(View.GONE);
                        locationClient.getLastLocation().addOnCompleteListener(onCompleteListener);
                        locationRefresh.setVisibility(View.VISIBLE);

                    }
                }
            });

        });

        return root;
    }

    private void setFavouriteStations(List<Station> stations) {

        Map<String, Boolean> favourites = getFavourite();

        ArrayList<StationWrapper> stationWrappersFav = new ArrayList<>();

        // Filter by favourites
        for (Station station : stations) {
            Boolean f = favourites.get(station.getRef_id());
            if (f == null) f = false;
            if (f) stationWrappersFav.add(new StationWrapper(station, true));
        }

        if (stationWrappersFav.size() == 0) {
            ((Activity)context).runOnUiThread(() -> {
                loc_err.setVisibility(View.GONE);
                fav_err.setVisibility(View.VISIBLE);
            });
        } else {
            ((Activity)context).runOnUiThread(() -> {
                fav_err.setVisibility(View.GONE);
                loc_err.setVisibility(View.GONE);
                adapter.setStations(stationWrappersFav);
            });
        }

    }

    private void setNearbyStations(List<Station> stations) {
        Map<String, Boolean> favourites = getFavourite();

        ArrayList<StationWrapper> stationWrappersFav = new ArrayList<>();


        if (location == null) {
            ((Activity)context).runOnUiThread(() -> {
                fav_err.setVisibility(View.GONE);
                loc_err.setVisibility(View.VISIBLE);
            });
        } else {

            Collections.sort(stations, (o1, o2) -> {
                double o1D = MapUtility.calculationByDistance(new LatLng(location.getLatitude(), location.getLongitude()), o1.getLatLng());
                double o2D = MapUtility.calculationByDistance(new LatLng(location.getLatitude(), location.getLongitude()), o2.getLatLng());
                return Double.compare(o1D, o2D);
            });
            for (Station station : stations) {
                Boolean f = favourites.get(station.getRef_id());
                if (f == null) f = false;
                stationWrappersFav.add(new StationWrapper(station, f, (int) Math.round(MapUtility.calculationByDistance(new LatLng(location.getLatitude(), location.getLongitude()), station.getLatLng()) * 1000)));
            }

            ((Activity)context).runOnUiThread(() -> {
                fav_err.setVisibility(View.GONE);
                loc_err.setVisibility(View.GONE);

                adapter.setStations(stationWrappersFav.subList(0, 30));
            });
        }
    }

    private Map<String, Boolean> getFavourite() {
        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences("station_favourites", MODE_PRIVATE);
        return (Map<String, Boolean>) sharedPreferences.getAll();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
    }

    public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public List<StationWrapper> stations;

        private Adapter() {
            stations = new ArrayList<>();
        }

        private void setStations(List<StationWrapper> stations) {
            this.stations = stations;
            notifyDataSetChanged();
        }
        public ArrayList<Station> getStations() {
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
            else
                if (station.distance < 900) distance = station.distance + " m";
                else distance = String.format(Locale.getDefault(), "%.2f km", station.distance / 1000f);

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
                fav = itemView.findViewById(R.id.station_nearby_favourite);

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



}
