package com.VegaSolutions.lpptransit.ui.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.animation.LayoutTransition;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Station;
import com.VegaSolutions.lpptransit.ui.Colors;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TestActivity extends AppCompatActivity {

    Adapter adapter;
    Location location = null;
    String filter = "";
    boolean fav = true;

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            TestActivity.this.location = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        adapter = new Adapter();

        RecyclerView rv = findViewById(R.id.test_rv);
        FrameLayout header = findViewById(R.id.header);
        TextSwitcher switcher = findViewById(R.id.station_title);
        FloatingActionButton fab = findViewById(R.id.sort_by_location);

        fab.setOnClickListener(view -> {
            switcher.setText("Postaje");
            if (fav) {
                if (location != null) {
                    Collections.sort(adapter.stationsCopy, (o1, o2) -> {
                        double d1 = calculationByDistance(o1.station.getLatLng(), new LatLng(location.getLatitude(), location.getLongitude()));
                        double d2 = calculationByDistance(o2.station.getLatLng(), new LatLng(location.getLatitude(), location.getLongitude()));
                        return Double.compare(d1, d2);
                    });
                    runOnUiThread(() -> {
                        adapter.setStations(adapter.stationsCopy);
                        adapter.filter(filter);
                    });
                    fav = false;
                } else
                    Toast.makeText(this, "Lokacija se ni znana", Toast.LENGTH_SHORT).show();
            } else {
                fav = true;
                SharedPreferences sharedPreferences = getSharedPreferences("station_favourites", MODE_PRIVATE);
                Map<String, Boolean> favourites = (Map<String, Boolean>) sharedPreferences.getAll();
                ArrayList<StationWrapper> stationWrappers = new ArrayList<>();
                ArrayList<StationWrapper> stationWrappersFav = new ArrayList<>();
                for (StationWrapper station : adapter.stationsCopy) {
                    if (station.favourite) stationWrappersFav.add(station);
                    else stationWrappers.add(station);
                }
                stationWrappersFav.addAll(stationWrappers);
                runOnUiThread(() -> {
                    adapter.setStations(stationWrappersFav);
                    adapter.filter(filter);
                });
            }
        });


        switcher.setFactory(() -> {
            TextView textView = new TextView(getApplicationContext());
            textView.setTextAppearance(this, R.style.robotoBoldTitle);
            return textView;
        });
        switcher.setCurrentText("Postaje");

        switcher.setInAnimation(getApplicationContext(), android.R.anim.slide_in_left);
        switcher.setOutAnimation(getApplicationContext(), android.R.anim.slide_out_right);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
                return;
            }
        }
        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000,
                10, locationListener);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10, locationListener);

        SearchView searchView = findViewById(R.id.station_search);
        searchView.setOnSearchClickListener(v -> {
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) searchView.getLayoutParams();
            params.width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
            searchView.setLayoutParams(params);
        });
        searchView.setOnCloseListener(() -> {
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) searchView.getLayoutParams();
            params.width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
            searchView.setLayoutParams(params);
            return false;
        });


        ConstraintLayout constraintLayout = findViewById(R.id.header_layout);
        constraintLayout.setLayoutTransition(new LayoutTransition());


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(adapter);
        rv.setNestedScrollingEnabled(false);
        rv.setHasFixedSize(true);
        rv.setItemViewCacheSize(20);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rv.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                header.setSelected(rv.canScrollVertically(-1));
                int i = linearLayoutManager.findFirstVisibleItemPosition();

                if (i > 0) {
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
                                switcher.setText("Priljibljene postaje");
                        } else {
                            if (tv.getText().equals("Priljibljene postaje"))
                                switcher.setText("Postaje");
                        }
                    }
                }
            });
        }


        Api.stationDetails(true, (apiResponse, statusCode, success) -> {
            if (success) {
                if (apiResponse.isSuccess()) {
                    SharedPreferences sharedPreferences = getSharedPreferences("station_favourites", MODE_PRIVATE);
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
                    runOnUiThread(() -> adapter.setStations(stationWrappersFav));
                }
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("station_favourites", MODE_PRIVATE);
        Map<String, Boolean> favourites = (Map<String, Boolean>) sharedPreferences.getAll();
        for (StationWrapper stationWrapper : adapter.stationsCopy) {
            Boolean f = favourites.get(stationWrapper.station.getRef_id());
            if (f == null)
                f = false;
            stationWrapper.favourite = f;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 0) {
            Intent i = getIntent();
            finish();
            startActivity(i);

        }
    }

    private class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<StationWrapper> stations;
        List<StationWrapper> stationsCopy;

        public Adapter() {
            stations = new ArrayList<>();
            stationsCopy = new ArrayList<>();
        }

        private void setStations(List<StationWrapper> stations) {
            this.stations = stations;
            this.stationsCopy = new ArrayList<>(stations);
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
            else
                distance = "?";

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
                Intent intent = new Intent(TestActivity.this, StationActivity.class);
                intent.putExtra("station_code", station.station.getRef_id());
                intent.putExtra("station_name", station.station.getName());
                intent.putExtra("station_distance", distance);
                intent.putExtra("station_center", Integer.valueOf(station.station.getRef_id()) % 2 != 0);
                startActivity(intent);
            });

            viewHolder.fav.setVisibility(station.favourite? View.VISIBLE : View.GONE);


        }

        @Override
        public int getItemCount() {
            return stations.size();
        }

        void filter(String text) {
            stations.clear();
            if(text.isEmpty()){
                stations.addAll(stationsCopy);
            } else{
                text = text.toLowerCase().replace('č', 'c').replace('š', 's').replace('ž', 'z');
                for(StationWrapper itemWr: stationsCopy){
                    Station item = itemWr.station;
                    String itemName = item.getName().toLowerCase().replace('č', 'c').replace('š', 's').replace('ž', 'z');
                    if (itemName.contains(text))
                        stations.add(itemWr);
                }
            }
            notifyDataSetChanged();
            filter = text;
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
