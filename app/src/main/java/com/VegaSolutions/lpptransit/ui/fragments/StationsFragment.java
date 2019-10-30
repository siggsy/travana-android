package com.VegaSolutions.lpptransit.ui.fragments;

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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Station;
import com.VegaSolutions.lpptransit.ui.Colors;
import com.VegaSolutions.lpptransit.ui.activities.StationActivity;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class StationsFragment extends Fragment {

    private StationsFragmentListener mListener;
    private Context context;
    private Location location;

    private FrameLayout header;
    private TextSwitcher switcher;
    private RecyclerView rv;

    private Adapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private boolean fav = true;


    public StationsFragment() {
    }


    public static StationsFragment newInstance() {
        StationsFragment fragment = new StationsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_stations, container, false);

        rv = root.findViewById(R.id.test_rv);
        header = root.findViewById(R.id.header);
        switcher = root.findViewById(R.id.station_title);

        setupUI();

        Api.stationDetails(false, (apiResponse, statusCode, success) -> {
            if (success) {
                SharedPreferences sharedPreferences = context.getSharedPreferences("station_favourites", MODE_PRIVATE);
                Map<String, Boolean> favourites = (Map<String, Boolean>) sharedPreferences.getAll();
                ArrayList<StationWrapper> stationWrappers = new ArrayList<>();
                ArrayList<StationWrapper> stationWrappersFav = new ArrayList<>();
                for (Station station : apiResponse.getData()) {
                    Boolean f = favourites.get(station.getRef_id());
                    if (f == null)
                        f = false;
                    if (f)
                        stationWrappersFav.add(new StationWrapper(station, true));
                    else
                        stationWrappers.add(new StationWrapper(station, false));
                }
                stationWrappersFav.addAll(stationWrappers);
                ((Activity)context).runOnUiThread(() -> adapter.setStations(stationWrappersFav));
                mListener.onStationsUpdated(apiResponse.getData().subList(0, 20));
            }
        });

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListener.onStationsUpdated(new ArrayList<>());
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    private void setupUI() {

        adapter = new Adapter();

        // TextSwitcher
        switcher.setFactory(() -> {
            TextView textView = new TextView(context);
            textView.setTextAppearance(context, R.style.robotoBoldTitle);
            return textView;
        });
        switcher.setCurrentText("Postaje");
        switcher.setInAnimation(context.getApplicationContext(), android.R.anim.slide_in_left);
        switcher.setOutAnimation(context.getApplicationContext(), android.R.anim.slide_out_right);

        // RecyclerView
        linearLayoutManager = new LinearLayoutManager(context);
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(adapter);
        rv.setNestedScrollingEnabled(true);
        rv.setHasFixedSize(true);
        rv.setItemViewCacheSize(20);
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                header.setSelected(rv.canScrollVertically(-1));
                int i = linearLayoutManager.findFirstVisibleItemPosition();

                if (i >= 0) {
                    if (!fav) {
                        StationWrapper station = adapter.stations.get(i);
                        if (location != null) {
                            TextView tv = (TextView) switcher.getCurrentView();
                            if (station.distance > 500) {
                                if (tv.getText().equals("Postaje v bližini"))
                                    switcher.setText("Postaje");
                            } else {
                                if (tv.getText().equals("Postaje"))
                                    switcher.setText("Postaje v bližini");
                            }
                        }
                    } else {
                        StationWrapper station = adapter.stations.get(i);
                        TextView tv = (TextView) switcher.getCurrentView();
                        if (station.favourite) {
                            if (tv.getText().equals("Postaje"))
                                switcher.setText("Priljubljene postaje");
                        } else {
                            if (tv.getText().equals("Priljubljene postaje"))
                                switcher.setText("Postaje");
                        }
                    }
                }
            }
        });

    }

    // Adapter class
    private class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<StationWrapper> stations;

        public Adapter() {
            stations = new ArrayList<>();
        }

        private void setStations(List<StationWrapper> stations) {
            this.stations = stations;
            notifyDataSetChanged();
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

            viewHolder.fav.setVisibility(station.favourite? View.VISIBLE : View.GONE);


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

}
