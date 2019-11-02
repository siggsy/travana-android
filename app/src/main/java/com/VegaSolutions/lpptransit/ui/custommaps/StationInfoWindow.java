package com.VegaSolutions.lpptransit.ui.custommaps;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Station;
import com.VegaSolutions.lpptransit.ui.Colors;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

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
        LinearLayout card;
        View divider;

        name = view.findViewById(R.id.station_nearby_name);
        distance = view.findViewById(R.id.station_nearby_distance);
        routes = view.findViewById(R.id.station_nearby_ll);
        center = view.findViewById(R.id.station_nearby_center);
        fav = view.findViewById(R.id.station_nearby_favourite);
        card = view.findViewById(R.id.station_nearby_card);
        divider = view.findViewById(R.id.station_nearby_devider);

        divider.setVisibility(View.GONE);

        name.setText(station.getName());
        distance.setText("?");

        for (String route : station.getRoute_groups_on_station()) {

            int color = Integer.valueOf(route);

            View v = context.getLayoutInflater().inflate(R.layout.template_route_number, routes, false);
            ((TextView) v.findViewById(R.id.route_station_number)).setText(route);

            v.findViewById(R.id.route_station_circle).getBackground().setColorFilter(new PorterDuffColorFilter(Colors.colors.get(color), PorterDuff.Mode.SRC_ATOP));
            routes.addView(v);

        }
        center.setVisibility(Integer.valueOf(station.getRef_id()) % 2 == 0 ? View.GONE : View.VISIBLE);

        SharedPreferences sharedPreferences = context.getSharedPreferences("station_favourites", MODE_PRIVATE);
        Map<String, Boolean> favourites = (Map<String, Boolean>) sharedPreferences.getAll();
        Boolean f = favourites.get(station.getRef_id());
        if (f == null) f = false;
        fav.setVisibility(f ? View.VISIBLE : View.GONE);

        return view;

    }
}
