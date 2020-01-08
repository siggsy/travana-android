package com.VegaSolutions.lpptransit.utility;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class LppHelper {

    public static final String ROUTE_FAVOURITES = "route_favourites";
    public static final String STATION_FAVOURITES = "station_favourites";

    public static Map<String, Boolean> getFavouriteRoutes(@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(ROUTE_FAVOURITES, MODE_PRIVATE);
        return (Map<String, Boolean>) sharedPreferences.getAll();
    }

    public static Map<String, Boolean> getFavouriteStations(@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(STATION_FAVOURITES, MODE_PRIVATE);
        return (Map<String, Boolean>) sharedPreferences.getAll();
    }

}
