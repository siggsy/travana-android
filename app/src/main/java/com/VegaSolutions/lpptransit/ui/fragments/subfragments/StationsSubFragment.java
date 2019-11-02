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
import com.VegaSolutions.lpptransit.ui.fragments.StationsFragment;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.security.auth.callback.Callback;

import static android.content.Context.MODE_PRIVATE;

public class StationsSubFragment extends Fragment {

    private static final String TYPE = "type";

    public static final int TYPE_ALL = 0;
    public static final int TYPE_NEARBY = 1;
    public static final int TYPE_FAVOURITE = 2;

    private boolean resumed = false;

    private int type;

    private Location location;
    private Adapter adapter = new Adapter();

    private StationsFragmentListener mListener;
    private Context context;
    private FragmentReadyCallback callback;

    public StationsSubFragment() {
    }

    public static StationsSubFragment newInstance(int type, FragmentReadyCallback callback) {
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
        callback.onFragmentResume(this);
        //mListener.onStationsUpdated(adapter.getStations());



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

                } else if (type == TYPE_ALL) {

                    for (Station station : apiResponse.getData()) {
                        Boolean f = favourites.get(station.getRef_id());
                        if (f == null) f = false;
                        stationWrappersFav.add(new StationWrapper(station, f));
                    }

                } else if (type == TYPE_NEARBY) {

                    if (location == null) {
                        // TODO: handle error
                        //((Activity) context).runOnUiThread(() -> mListener.onStationsUpdated(new ArrayList<>()));
                        //return;
                    }

                    //TODO: sort by location

                    for (Station station : apiResponse.getData()) {
                        Boolean f = favourites.get(station.getRef_id());
                        if (f == null) f = false;
                        stationWrappersFav.add(new StationWrapper(station, f));
                    }


                }

                ((Activity) context).runOnUiThread(() -> {
                    adapter.setStations(stationWrappersFav);
                    mListener.onStationsUpdated(adapter.getStations());
                    Log.i("Substation " + type, "updated " + resumed);
                });


            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        resumed = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_stations_sub, container, false);



        RecyclerView list = root.findViewById(R.id.stations_sub_list);
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(context));
        list.setHasFixedSize(false);


        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListener.onStationsUpdated(new ArrayList<>());
    }

    @Override
    public void onStart() {
        super.onStart();
        callback.onFragmentStart(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof StationsFragmentListener) {
            mListener = (StationsFragmentListener) context;
            this.context = context;
        } else throw new RuntimeException(context.toString() + " must implement StationsFragmentListener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        context = null;
    }

    public interface StationsFragmentListener {
        void onFragmentInteraction(Uri uri);
        void onStationsUpdated(List<Station> stations);
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
                    station.distance = (int) Math.round(calculationByDistance(station.station.getLatLng(), new LatLng(location.getLatitude(), location.getLongitude())) * 1000);
                distance = station.distance + " m";
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
            distance = -1;
            this.favourite = favourite;
            this.station = station;
        }

    }

    public static double calculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));

        return Radius * c;
    }

    public interface FragmentReadyCallback {
        void onFragmentResume(Fragment fragment);
        void onFragmentStart(Fragment fragment);
    }

}
