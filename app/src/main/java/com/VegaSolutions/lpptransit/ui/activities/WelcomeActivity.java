package com.VegaSolutions.lpptransit.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;

import com.VegaSolutions.lpptransit.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("settings", MODE_PRIVATE);
        boolean dark_theme = sharedPreferences.getBoolean("app_theme", false);
        setTheme(dark_theme ? R.style.DarkTheme : R.style.WhiteTheme);
        setContentView(R.layout.activity_welcome);

        ImageButton next_btn = findViewById(R.id.image_welcome_next_btn);

        next_btn.setOnClickListener(e ->{
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
        });

    }
}
