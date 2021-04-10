package com.VegaSolutions.lpptransit.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;

public class SettingsActivity extends AppCompatActivity {

    public static final int SETTINGS_UPDATE = 0;

    ImageView back;

    RadioButton buttonWhite, buttonDark, buttonMin, buttonHour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("settings", MODE_PRIVATE);
        boolean darkTheme = ViewGroupUtils.isDarkTheme(this);
        boolean hour = sharedPreferences.getBoolean("hour", false);
        setTheme(ViewGroupUtils.isDarkTheme(this) ? R.style.DarkTheme : R.style.WhiteTheme);
        setContentView(R.layout.activity_settings);

        back = findViewById(R.id.back);
        back.setOnClickListener(v -> onBackPressed());

        buttonDark = findViewById(R.id.radio_dark);
        buttonWhite = findViewById(R.id.radio_white);
        buttonMin = findViewById(R.id.radio_minute);
        buttonHour = findViewById(R.id.radio_hour);

        if (darkTheme) buttonDark.setChecked(true);
        else buttonWhite.setChecked(true);

        if (hour) buttonHour.setChecked(true);
        else buttonMin.setChecked(true);

        setResult(1);

        buttonDark.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                // Set dark theme
                setResult(SETTINGS_UPDATE);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("app_theme", true);
                editor.apply();

                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                finish();
            }

        });

        buttonWhite.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {

                // Set white theme
                setResult(SETTINGS_UPDATE);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("app_theme", false);
                editor.apply();

                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                finish();
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
