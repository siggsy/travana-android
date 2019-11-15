package com.VegaSolutions.lpptransit.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;

public class PostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ViewGroupUtils.isDarkTheme(this) ? R.style.DarkTheme : R.style.WhiteTheme);
        setContentView(R.layout.activity_post);
    }
}
