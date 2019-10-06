package com.VegaSolutions.lpptransit.ui.bottomfragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.ApiCallback;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.ApiResponse;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.ArrivalWrapper;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Station;
import com.VegaSolutions.lpptransit.lppapideprecated.LppQuery;
import com.VegaSolutions.lpptransit.lppapideprecated.responseclasses.LiveBusArrivalV2;
import com.VegaSolutions.lpptransit.lppapideprecated.responseclasses.StationsInRange;
import com.VegaSolutions.lpptransit.test.LiveArrival;
import com.VegaSolutions.lpptransit.test.MapsTestActivity;
import com.VegaSolutions.lpptransit.ui.viewmodels.LppSharedViewModel;
import com.VegaSolutions.lpptransit.ui.viewmodels.LppViewModel;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    private Context context;


    public static final int NEARBY_BUTTON = 0;
    public static final int ROUTES_BUTTON = 1;

    private OnFragmentInteractionListener mListener;
    private MainAdapter adapter;
    private LppViewModel model;

    public MainFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = ViewModelProviders.of((FragmentActivity) context).get(LppViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        ImageButton nearby = root.findViewById(R.id.main_fragment_in_range_btn);
        ImageButton routes = root.findViewById(R.id.main_fragment_routes_btn);
        RecyclerView mainRV = root.findViewById(R.id.main_fragment_rv);

        nearby.setOnClickListener(view -> onButtonPressed(NEARBY_BUTTON));
        routes.setOnClickListener(view -> onButtonPressed(ROUTES_BUTTON));



        adapter = new MainAdapter();
        mainRV.setAdapter(adapter);
        mainRV.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Station> stations = model.getStations().getValue();
        if (stations != null)
            adapter.setStations(stations);
        model.getStations().observe(this, stationsInRanges -> adapter.setStations(stationsInRanges));

        return root;
    }

    private void onButtonPressed(int uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(@androidx.annotation.NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        context = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(int uri);
    }

    private class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<NearbyStation> stations;

        private MainAdapter() {
            stations = new ArrayList<>();
        }

        private void setStations(List<Station> stations) {

            List<NearbyStation> stationsInRange = new ArrayList<>();
            for (Station station : stations) {
                NearbyStation nearbyStation = new NearbyStation(station, (int) Math.round(MapsTestActivity.CalculationByDistance(new LatLng(station.getLatitude(), station.getLongitude()), new LatLng(46.056319, 14.505381)) * 1000), "");
                stationsInRange.add(nearbyStation);
            }
            Collections.sort(stationsInRange, (nearbyStation, t1) -> Integer.compare(nearbyStation.distance, t1.distance));
            this.stations = stationsInRange;
            Log.i("stationsInRange", stationsInRange.toString());
            notifyDataSetChanged();
        }

        private NearbyStation getStation(int id) {
            for (NearbyStation station : stations)
                if (station.station.getInt_id() == id) return station;
            return null;
        }
        private int indexOf(int id) {
            for (int i = 0, stationsSize = stations.size(); i < stationsSize; i++) {
                NearbyStation station = stations.get(i);
                if (station.station.getInt_id() == id) return i;
            }
            return -1;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(getLayoutInflater().inflate(R.layout.adapter_main_fragment_station_in_range, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            NearbyStation station = stations.get(position);

            viewHolder.name.setText(station.station.getName());
            viewHolder.distance.setText(station.distance + " m");
            viewHolder.live.setText(station.arrivals);

            viewHolder.cv.setOnClickListener(view -> {
                Intent i = new Intent(context, LiveArrival.class);
                i.putExtra("station_id", station.station.getRef_id());
                context.startActivity(i);
            });

        }

        @Override
        public int getItemCount() {
            return stations.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            TextView name, live, distance;
            CardView cv;

            private ViewHolder(@NonNull View itemView) {
                super(itemView);

                name = itemView.findViewById(R.id.station_in_range_name_tv);
                live = itemView.findViewById(R.id.station_in_range_live_data_ts);
                distance = itemView.findViewById(R.id.station_in_range_distance_ts);
                cv = itemView.findViewById(R.id.station_in_range_cv);

                live.setSelected(true);

            }
        }

    }

    private class NearbyStation {

        private Station station;
        private int distance;
        private String arrivals;


        private NearbyStation(Station station, int distance, String arrivals) {
            this.station = station;
            this.distance = distance;
            this.arrivals = arrivals;
        }

    }

}
