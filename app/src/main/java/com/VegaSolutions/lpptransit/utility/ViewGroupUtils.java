package com.VegaSolutions.lpptransit.utility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.VegaSolutions.lpptransit.R;

import static android.content.Context.MODE_PRIVATE;

public class ViewGroupUtils {

    /**
     * Checks if dark theme is enabled
     * @param activity context used to check settings
     * @return boolean if dark theme is enabled
     */
    public static boolean isDarkTheme(Context activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("settings", MODE_PRIVATE);
        return sharedPreferences.getBoolean("app_theme", true);
    }

}