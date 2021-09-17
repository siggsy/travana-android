package com.VegaSolutions.lpptransit.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;

public class SettingsActivity extends AppCompatActivity {

    public static final int SETTINGS_UPDATE = 0;

    ImageView back;

    RadioButton buttonWhite, buttonDark, buttonAuto, buttonMin, buttonHour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("settings", MODE_PRIVATE);
        boolean darkTheme = ViewGroupUtils.isDarkTheme(this);
        boolean hour = sharedPreferences.getBoolean("hour", false);

        setContentView(R.layout.activity_settings);

        back = findViewById(R.id.back);
        back.setOnClickListener(v -> onBackPressed());

        buttonDark = findViewById(R.id.radio_dark);
        buttonWhite = findViewById(R.id.radio_white);
        buttonAuto = findViewById(R.id.radio_auto);
        buttonMin = findViewById(R.id.radio_minute);
        buttonHour = findViewById(R.id.radio_hour);

        String appTheme = sharedPreferences.getString("application_theme", ViewGroupUtils.Theme.NO.name());

        switch (ViewGroupUtils.Theme.valueOf(appTheme)) {
            case NO: buttonWhite.setChecked(true); break;
            case YES: buttonDark.setChecked(true); break;
            case AUTO: buttonAuto.setChecked(true); break;
        }

        if (hour) buttonHour.setChecked(true);
        else buttonMin.setChecked(true);

        buttonDark.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                // Set dark theme
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("application_theme", ViewGroupUtils.Theme.YES.name());
                editor.apply();
            }

        });

        buttonWhite.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {

                // Set white theme
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("application_theme", ViewGroupUtils.Theme.NO.name());
                editor.apply();
            }

        });

        buttonAuto.setVisibility(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ? View.VISIBLE : View.GONE);
        buttonAuto.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("application_theme", ViewGroupUtils.Theme.AUTO.name());
                editor.apply();
            }
        });

        buttonMin.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {

                // Set arrival to min to
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("hour", false);
                editor.apply();

            }

        });

        buttonHour.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {

                // Set arrival to time when
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("hour", true);
                editor.apply();

            }

        });

    }
}
