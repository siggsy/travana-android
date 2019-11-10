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
import com.google.android.gms.maps.model.LatLng;
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
    private LocationManager locationManager;
    private List<Station> stations;
    private boolean firstTime = true;


    private LocationListener myLocListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (context == null) return;

            StationsSubFragment.this.location = location;
            if (firstTime) {
                firstTime = false;
                setNearbyStations(stations);
                ((Activity)context).runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                return;
            }
            Snackbar snackbar = Snackbar.make(root, R.string.location_changed, Snackbar.LENGTH_INDEFINITE).setAction(R.string.update, v -> {
                setNearbyStations(stations);
                ((Activity)context).runOnUiThread(() -> progressBar.setVisibility(View.GONE));
            }).setActionTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            View view = snackbar.getView();
            TextView tv = view.findViewById(com.google.android.material.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snackbar.show();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            ((Activity)context).runOnUiThread(() -> {
                progressBar.setVisibility(View.VISIBLE);
                loc_err.setVisibility(View.GONE);
                if (location != null)
                    onLocationChanged(location);
            });
        }

        @Override
        public void onProviderDisabled(String provider) {
            firstTime = true;
            ((Activity)context).runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                loc_err.setVisibility(View.VISIBLE);
                adapter.setStations(new ArrayList<>());
            });
        }
    };

    private RecyclerView list;
    private View fav_err, loc_err, progressBar;
    View root;

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
        list.setHasFixedSize(true);
        list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                callback.onHeaderChanged(recyclerView.canScrollVertically(-1));
            }
        });
    }

    private void setupCurrentLocationUpdates() {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // Get the criteria you would like to use
        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setSpeedRequired(false);
        // Get the best provider from the criteria specified, and false to say it can turn the provider on if it isn't already

        bestProvider = locationManager.getBestProvider(criteria, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt(TYPE);
        }
        Log.i("SubStationFragment", "created");
        if (type == TYPE_NEARBY)
            setupCurrentLocationUpdates();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("SubStationFragment", "view destroyed");
        if (type == TYPE_NEARBY)
            locationManager.removeUpdates(myLocListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        callback.onHeaderChanged(list.canScrollVertically(-1));
        if (type == TYPE_FAVOURITE && stations != null) {
            adapter.setStations(new ArrayList<>());
            setFavouriteStations(stations);
        } else
            adapter.notifyDataSetChanged();
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

        setupUI();

        Api.stationDetails(false, (apiResponse, statusCode, success) -> {
            if (success) {

                stations = apiResponse.getData();
                if (context == null)
                    return;

                // Get favourites
                if (type == TYPE_FAVOURITE) {
                    setFavouriteStations(apiResponse.getData());
                    ((Activity)context).runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                } else if (type == TYPE_NEARBY) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            loc_err.setVisibility(View.VISIBLE);
                            return;
                        }
                    }
                    ((Activity)context).runOnUiThread(() -> locationManager.requestLocationUpdates(bestProvider, minTime, minDistance, myLocListener));
                }

            }
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

                adapter.setStations(stationWrappersFav.subList(0, 100));
            });
        }
    }

    private Map<String, Boolean> getFavourite() {
        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences("station_favourites", MODE_PRIVATE);
        return (Map<String, Boolean>) sharedPreferences.getAll();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (type == TYPE_NEARBY)
            locationManager.removeUpdates(myLocListener);
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
            if (type != TYPE_NEARBY)
                distance = "";
            else if (location != null) {
                if (station.distance == -1)
                    station.distance = (int) Math.round(MapUtility.calculationByDistance(station.station.getLatLng(), new LatLng(location.getLatitude(), location.getLongitude())) * 1000);
                if (station.distance < 900)
                    distance = station.distance + " m";
                else
                    distance = String.format(Locale.getDefault(), "%.2f km", station.distance / 1000f);
            }
            else distance = "?";

            // Update ViewHolder
            viewHolder.name.setText(station.station.getName());
            viewHolder.distance.setText(distance);
            viewHolder.center.setVisibility(Integer.valueOf(station.station.getRef_id()) % 2 == 0 ? View.GONE : View.VISIBLE);
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
