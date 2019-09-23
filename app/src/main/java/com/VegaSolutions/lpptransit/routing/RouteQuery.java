package com.VegaSolutions.lpptransit.routing;


import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RouteQuery extends AsyncTask<String, Void, Void> {

    private String url = "http://10.0.1.61:5000/match/v1/driving/";
    private String coordinatesString;
    private String radiuses;
    private RouteListener routeListener = (route, httpStatus, success) -> {
        Log.e("RouteQuery", route.matchings.get(0).toString());
    };
    private List<LatLng> coordinates = new ArrayList<>();

    public RouteQuery addCoordinates(LatLng... latLngs) {
        coordinates.addAll(Arrays.asList(latLngs));

        StringBuilder coor = new StringBuilder();
        StringBuilder radiuses = new StringBuilder();
        for (LatLng latLng : coordinates) {
            coor.append(latLng.longitude).append(",").append(latLng.latitude).append(';');
            radiuses.append(20).append(';');
        }
        coordinatesString = coor.substring(0, coor.length() - 1);
        this.radiuses = radiuses.substring(0, radiuses.length() -1);
        return this;

    }

    public RouteQuery addListener(RouteListener routeListener) {
        this.routeListener = routeListener;
        return this;
    }

    @Override
    protected Void doInBackground(String... strings) {

        try {
            Log.e("quert", url + coordinatesString + "?geometries=geojson&overview=false&radiuses=" + radiuses);
            Connection.Response r = Jsoup.connect(url + coordinatesString + "?geometries=geojson&overview=full&radiuses=" + radiuses).ignoreContentType(true).execute();
            Log.e("RouteQuery", r.body());
            Gson gson = new Gson();
            MatchingsResponse route = gson.fromJson(r.body(), MatchingsResponse.class);
            routeListener.onComplete(route, r.statusCode(), true);
        } catch (HttpStatusException e) {
            e.printStackTrace();
            routeListener.onComplete(null, e.getStatusCode(), false);
        } catch (IOException e) {
            e.printStackTrace();
            routeListener.onComplete(null, -1, false);
        }

        return null;
    }

    public interface RouteListener {
        void onComplete(MatchingsResponse routeResponse, int httpStatus, boolean success);
    }

}
