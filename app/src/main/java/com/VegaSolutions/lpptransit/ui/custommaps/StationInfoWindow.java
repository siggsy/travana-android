package com.VegaSolutions.lpptransit.ui.custommaps;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Station;
import com.VegaSolutions.lpptransit.ui.Colors;
import com.VegaSolutions.lpptransit.utility.MapUtility;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class StationInfoWindow implements GoogleMap.InfoWindowAdapter {

    private Activity context;

    public StationInfoWindow(Activity context) {
        this.context = context;
    }


    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {

        Station station = (Station) marker.getTag();
        if (station == null)
            return null;

        View view =  context.getLayoutInflater().inflate(R.layout.template_station_nearby,null);
        TextView name, distance, center;
        FlexboxLayout routes;
        ImageView fav;
        View divider;
        LinearLayout root;

        SharedPreferences sharedPreferences = context.getSharedPreferences("station_favourites", MODE_PRIVATE);
        Map<String, Boolean> favourites = (Map<String, Boolean>) sharedPreferences.getAll();
        Boolean f = favourites.get(station.getRef_id());
        if (f == null) f = false;

        name = view.findViewById(R.id.station_nearby_name);
        distance = view.findViewById(R.id.station_nearby_distance);
        routes = view.findViewById(R.id.station_nearby_ll);
        center = view.findViewById(R.id.station_nearby_center);
        fav = view.findViewById(R.id.station_nearby_favourite);
        divider = view.findViewById(R.id.station_nearby_devider);
        root = view.findViewById(R.id.station_nearby_card);

        divider.setVisibility(View.GONE);
        name.setText(station.getName());
        name.setTextColor(Color.BLACK);
        distance.setText("");
        center.setVisibility(Integer.valueOf(station.getRef_id()) % 2 == 0 ? View.GONE : View.VISIBLE);
        fav.setVisibility(f ? View.VISIBLE : View.GONE);

        for (String route : station.getRoute_groups_on_station()) {

            View v = context.getLayoutInflater().inflate(R.layout.template_route_number, routes, false);
            TextView textView =  v.findViewById(R.id.route_station_number);
            textView.setText(route);
            textView.setTextColor(Color.WHITE);
            v.findViewById(R.id.route_station_circle).getBackground().setTint(Colors.getColorFromString(route));
            routes.addView(v);

        }

        return view;

    }
}