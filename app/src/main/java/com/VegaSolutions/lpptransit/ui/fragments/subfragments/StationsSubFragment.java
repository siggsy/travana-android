package com.VegaSolutions.lpptransit.ui.fragments.subfragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.ApiCallback;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.ApiResponse;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Station;
import com.VegaSolutions.lpptransit.ui.Colors;
import com.VegaSolutions.lpptransit.ui.activities.StationActivity;
import com.VegaSolutions.lpptransit.ui.fragments.FragmentHeaderCallback;
import com.VegaSolutions.lpptransit.ui.fragments.StationsFragment;
import com.VegaSolutions.lpptransit.utility.MapUtility;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    private boolean resumed = false;

    private int type;

    private Location location;
    private Adapter adapter = new Adapter();

    private StationsFragmentListener mListener;
    private Context context;
    private FragmentHeaderCallback callback;
    private FusedLocationProviderClient locationProviderClient;
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult.getLastLocation() == null)
                return;
            location = locationResult.getLastLocation();
            Collections.sort(adapter.stations, (o1, o2) -> Double.compare(o1.distance, o2.distance));

            ((Activity) context).runOnUiThread(() -> {
                adapter.notifyDataSetChanged();
                Log.i("Substation " + type, "updated");
            });
        }
    };

    private RecyclerView list;

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
        Log.i("SubStationFragment", "created");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("SubStationFragment", "view destroyed");
    }

    @Override
    public void onResume() {
        super.onResume();

        callback.onHeaderChanged(list.canScrollVertically(-1));

        //startCurrentLocationUpdates();

        Api.stationDetails(false, (apiResponse, statusCode, success) -> {
            if (success) {

                SharedPreferences sharedPreferences = context.getSharedPreferences("station_favourites", MODE_PRIVATE);
                Map<String, Boolean> favourites = (Map<String, Boolean>) sharedPreferences.getAll();
                ArrayList<StationWrapper> stationWrappersFav = new ArrayList<>();
                if (type == TYPE_FAVOURITE) {

                    if (favourites.size() == 0) {
                        // TODO: write msg "no favourites added yet"
                        return;
                    }

                    for (Station station : apiResponse.getData()) {
                        Boolean f = favourites.get(station.getRef_id());
                        if (f == null) f = false;
                        if (f) stationWrappersFav.add(new StationWrapper(station, true));
                    }

                    ((Activity) context).runOnUiThread(() -> {
                        adapter.setStations(stationWrappersFav);
                        mListener.onStationsUpdated(adapter.getStations());
                        Log.i("Substation " + type, "updated " + resumed);
                    });

                } else if (type == TYPE_NEARBY) {

                    if (location == null) {
                        // TODO: handle error
                        locationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                location = task.getResult();
                                Collections.sort(apiResponse.getData(), (o1, o2) -> {

                                    double o1D = MapUtility.calculationByDistance(new LatLng(location.getLatitude(), location.getLongitude()), o1.getLatLng());
                                    double o2D = MapUtility.calculationByDistance(new LatLng(location.getLatitude(), location.getLongitude()), o2.getLatLng());

                                    return Double.compare(o1D, o2D);

                                });

                                for (Station station : apiResponse.getData()) {
                                    Boolean f = favourites.get(station.getRef_id());
                                    if (f == null) f = false;
                                    stationWrappersFav.add(new StationWrapper(station, f, (int) Math.round(MapUtility.calculationByDistance(new LatLng(location.getLatitude(), location.getLongitude()), station.getLatLng()) * 1000)));
                                }
                                ((Activity) context).runOnUiThread(() -> {
                                    adapter.setStations(stationWrappersFav);
                                    mListener.onStationsUpdated(adapter.getStations());
                                    Log.i("Substation " + type, "updated " + resumed);
                                });
                            }
                        });
                    }else {
                        Collections.sort(apiResponse.getData(), (o1, o2) -> {

                            double o1D = MapUtility.calculationByDistance(new LatLng(location.getLatitude(), location.getLongitude()), o1.getLatLng());
                            double o2D = MapUtility.calculationByDistance(new LatLng(location.getLatitude(), location.getLongitude()), o2.getLatLng());

                            return Double.compare(o1D, o2D);

                        });

                        for (Station station : apiResponse.getData()) {
                            Boolean f = favourites.get(station.getRef_id());
                            if (f == null) f = false;
                            stationWrappersFav.add(new StationWrapper(station, f, (int) Math.round(MapUtility.calculationByDistance(new LatLng(location.getLatitude(), location.getLongitude()), station.getLatLng()) * 1000)));
                        }
                        ((Activity) context).runOnUiThread(() -> {
                            adapter.setStations(stationWrappersFav);
                            mListener.onStationsUpdated(adapter.getStations());
                            Log.i("Substation " + type, "updated " + resumed);
                        });
                    }

                }

            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        locationProviderClient.removeLocationUpdates(locationCallback);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_stations_sub, container, false);

        list = root.findViewById(R.id.stations_sub_list);
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




        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListener.onStationsUpdated(new ArrayList<>());
        locationProviderClient.removeLocationUpdates(locationCallback);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof StationsFragmentListener) {
            mListener = (StationsFragmentListener) context;
            this.context = context;
            locationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        } else throw new RuntimeException(context.toString() + " must implement StationsFragmentListener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        context = null;
        locationProviderClient  = null;
    }

    public interface StationsFragmentListener {
        void onFragmentInteraction(Uri uri);
        void onStationsUpdated(List<Station> stations);
    }

    private void startCurrentLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
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

            String distance;
            if (location != null) {
                if (station.distance == -1)
                    station.distance = (int) Math.round(MapUtility.calculationByDistance(station.station.getLatLng(), new LatLng(location.getLatitude(), location.getLongitude())) * 1000);
                if (station.distance < 900)
                    distance = station.distance + " m";
                else
                    distance = String.format(Locale.getDefault(), "%.2f km", station.distance / 1000f);
            }
            else distance = "?";

            viewHolder.name.setText(station.station.getName());
            viewHolder.distance.setText(distance);
            viewHolder.routes.removeAllViews();
            for (String route : station.station.getRoute_groups_on_station()) {

                String group = route.replaceAll("[^0-9]", "");
                int color = Integer.valueOf(group);

                View v = getLayoutInflater().inflate(R.layout.template_route_number, viewHolder.routes, false);
                ((TextView) v.findViewById(R.id.route_station_number)).setText(route);

                viewHolder.routes.addView(v);
                v.findViewById(R.id.route_station_circle).getBackground().setColorFilter(new PorterDuffColorFilter(Colors.colors.get(color), PorterDuff.Mode.SRC_ATOP));

            }

            viewHolder.center.setVisibility(Integer.valueOf(station.station.getRef_id()) % 2 == 0 ? View.GONE : View.VISIBLE);

            viewHolder.card.setOnClickListener(v -> {
                Intent intent = new Intent(context, StationActivity.class);
                intent.putExtra("station_code", station.station.getRef_id());
                intent.putExtra("station_name", station.station.getName());
                intent.putExtra("station_center", Integer.valueOf(station.station.getRef_id()) % 2 != 0);
                startActivity(intent);
            });

            viewHolder.fav.setVisibility(station.favourite ? View.VISIBLE : View.GONE);


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
