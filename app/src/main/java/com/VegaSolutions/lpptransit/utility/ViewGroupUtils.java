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

    public static ViewGroup getParent(View view) {
        return (ViewGroup)view.getParent();
    }

    public static void removeView(View view) {
        ViewGroup parent = getParent(view);
        if(parent != null) {
            parent.removeView(view);
        }
    }

    public static void replaceView(View currentView, View newView) {
        ViewGroup parent = getParent(currentView);
        if(parent == null) {
            return;
        }
        final int index = parent.indexOfChild(currentView);
        ViewGroup.LayoutParams params = currentView.getLayoutParams();
        newView.setLayoutParams(params);
        removeView(currentView);
        removeView(newView);
        parent.addView(newView, index);
    }

    public static boolean isDarkTheme(Context activity) {

        SharedPreferences sharedPreferences = activity.getSharedPreferences("settings", MODE_PRIVATE);
        return sharedPreferences.getBoolean("app_theme", false);

    }

}