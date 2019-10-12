package com.VegaSolutions.lpptransit.ui.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Station;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    List<Integer> colors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Adapter adapter = new Adapter();

        RecyclerView rv = findViewById(R.id.test_rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        rv.setNestedScrollingEnabled(false);

        Api.stationDetails(true, (apiResponse, statusCode, success) -> {
            if (success) {
                if (apiResponse.isSuccess()) {
                    runOnUiThread(() -> adapter.setStations(apiResponse.getData()));
                }
            }
        });

        colors = new ArrayList<>();

        colors.add(Color.HSVToColor(new float[] {123.064f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {96.511f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {113.118f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {101.539f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {86.082f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {128.590f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {63.331f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {133.131f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {95.947f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {108.090f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {59.818f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {117.424f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {83.369f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {61.190f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {149.889f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {58.856f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {139.150f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {74.911f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {94.721f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {133.204f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {104.606f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {86.916f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {50.519f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {102.856f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {137.366f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {92.141f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {65.918f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {54.480f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {101.223f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {83.907f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {127.487f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {117.321f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {132.022f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {62.352f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {105.308f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {63.348f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {99.404f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {134.938f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {58.938f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {102.779f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {117.037f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {138.501f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {115.415f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {129.359f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {145.784f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {105.187f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {61.708f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {114.606f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {50.624f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {108.683f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {137.593f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {53.135f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {76.974f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {61.758f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {78.538f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {145.463f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {68.549f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {100.180f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {114.768f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {55.466f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {69.098f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {50.869f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {133.828f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {97.522f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {126.700f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {62.948f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {127.947f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {93.483f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {67.696f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {52.142f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {72.412f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {103.476f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {91.236f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {102.085f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {50.723f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {90.677f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {143.682f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {119.939f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {136.312f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {58.937f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {81.891f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {144.980f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {51.706f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {129.601f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {102.981f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {69.425f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {133.657f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {77.891f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {110.629f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {122.447f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {83.349f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {123.315f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {106.969f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {123.269f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {56.037f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {76.395f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {99.571f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {132.755f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {73.408f , 20f, 20f}));
        colors.add(Color.HSVToColor(new float[] {87.315f , 20f, 20f}));


    }

    private class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<Station> stations;

        public Adapter() {
            stations = new ArrayList<>();
        }

        private void setStations(List<Station> stations) {
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

            Station station = stations.get(position);
            ViewHolder viewHolder = (ViewHolder) holder;

            viewHolder.name.setText(station.getName());
            viewHolder.distance.setText(Math.round(calculationByDistance(station.getLatLng(), new LatLng(46.056319, 14.505381)) * 1000) + " m");
            viewHolder.routes.removeAllViews();
            for (String route : station.getRoute_groups_on_station()) {

                String group = route.replaceAll("[^0-9]", "");
                int color = Integer.valueOf(group);
                View v = getLayoutInflater().inflate(R.layout.template_route_station, viewHolder.routes, false);
                ((TextView) v.findViewById(R.id.route_station_number)).setText(route);
                viewHolder.routes.addView(v);
                ((ImageView) v.findViewById(R.id.route_station_circle)).setColorFilter(colors.get(color));
            }

            viewHolder.centerBackg.setVisibility(Integer.valueOf(station.getRef_id()) % 2 == 0 ? View.GONE : View.VISIBLE);

            ArrayList<Integer>[] al = new ArrayList[0];


        }

        @Override
        public int getItemCount() {
            return stations.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            TextView name, distance;
            LinearLayout routes;

            ImageView centerBackg;

            CardView card;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                name = itemView.findViewById(R.id.station_nearby_name);
                distance = itemView.findViewById(R.id.station_nearby_distance);
                routes = itemView.findViewById(R.id.station_nearby_ll);
                card = itemView.findViewById(R.id.station_nearby_card);
                centerBackg = itemView.findViewById(R.id.station_nearby_center_background);

            }
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
