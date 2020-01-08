package com.VegaSolutions.lpptransit.ui.custommaps;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Station;
import com.VegaSolutions.lpptransit.utility.Colors;
import com.VegaSolutions.lpptransit.utility.LppHelper;
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
        View divider;
        LinearLayout root;

        Map<String, Boolean> favourites = LppHelper.getFavouriteStations(context);
        Boolean f = favourites.get(station.getRef_id());
        if (f == null) f = false;

        name = view.findViewById(R.id.station_nearby_name);
        distance = view.findViewById(R.id.station_nearby_distance);
        routes = view.findViewById(R.id.station_nearby_ll);
        center = view.findViewById(R.id.station_nearby_center);
        fav = view.findViewById(R.id.route_favourite);
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
