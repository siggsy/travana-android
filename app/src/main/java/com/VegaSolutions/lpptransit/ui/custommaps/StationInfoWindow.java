package com.VegaSolutions.lpptransit.ui.custommaps;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Station;
import com.VegaSolutions.lpptransit.utility.Colors;
import com.VegaSolutions.lpptransit.utility.LppHelper;
import com.VegaSolutions.lpptransit.utility.MapUtility;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.Map;

public class StationInfoWindow implements GoogleMap.InfoWindowAdapter {

    private final Activity context;
    private LocationManager locationManager;

    public StationInfoWindow(Activity context) {
        this.context = context;
    }


    @Override
    public View getInfoWindow(Marker marker) {
        Station station = (Station) marker.getTag();
        if (station == null)
            return null;

        View view = context.getLayoutInflater().inflate(R.layout.station_infowindow, null);
        TextView name, distance, center;
        FlexboxLayout routes;
        ImageView fav;
        RelativeLayout root;

        Map<String, Boolean> favourites = LppHelper.getFavouriteStations(context);
        Boolean f = favourites.get(station.getRef_id());
        if (f == null) f = false;

        name = view.findViewById(R.id.station_nearby_name);
        distance = view.findViewById(R.id.station_nearby_distance);
        routes = view.findViewById(R.id.station_nearby_ll);
        center = view.findViewById(R.id.station_nearby_center);
        fav = view.findViewById(R.id.route_favourite);
        root = view.findViewById(R.id.station_nearby_card);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        
        try {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                double distanceA = MapUtility.calculationByDistance(location.getLatitude(), location.getLongitude(), station.getLatitude(), station.getLongitude());

                if (distanceA < 0.9) {
                    distanceA *= 1000;
                    distance.setText((int) distanceA + " m");
                } else {
                    distanceA = (double) Math.round(distanceA * 100d) / 100d;
                    distance.setText(distanceA + " km");
                }
            }
        } catch (SecurityException e) {
            distance.setText("");
        }


        name.setText(station.getName());
        center.setVisibility(Integer.parseInt(station.getRef_id()) % 2 == 0 ? View.GONE : View.VISIBLE);
        fav.setVisibility(f ? View.VISIBLE : View.GONE);

        for (String route : station.getRoute_groups_on_station()) {

            View v = LayoutInflater.from(context).inflate(R.layout.template_route_number, null);
            TextView textView = v.findViewById(R.id.route_station_number);
            textView.setText(route);
            textView.setTextColor(Color.WHITE);
            v.findViewById(R.id.route_station_circle).getBackground().setTint(Colors.getColorFromString(route));
            routes.addView(v);

        }

        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }


}
