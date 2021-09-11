package com.VegaSolutions.lpptransit.utility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.VegaSolutions.lpptransit.R;

import static android.content.Context.MODE_PRIVATE;

import androidx.appcompat.app.AppCompatDelegate;

public class ViewGroupUtils {

    public enum Theme {
        NO(AppCompatDelegate.MODE_NIGHT_NO),
        YES(AppCompatDelegate.MODE_NIGHT_YES),
        AUTO(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        public final int value;
        Theme(int value) {
            this.value = value;
        }
    }

    /**
     * Checks if dark theme is enabled
     * @param activity context used to check settings
     * @return boolean if dark theme is enabled
     */
    public static boolean isDarkTheme(Context activity) {
        return Configuration.UI_MODE_NIGHT_YES == (activity.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK);
    }

}