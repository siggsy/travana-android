package com.VegaSolutions.lpptransit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.VegaSolutions.lpptransit.lppapi.LppQuery;

import java.util.HashMap;
import java.util.Map;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // Set log TextView

        TextView log = findViewById(R.id.test_log_tv);
        log.setMovementMethod(new ScrollingMovementMethod());


        // Query test

        Map<String, String> params = new HashMap<>();
        params.put("bus_id", "LPP-101");

        LppQuery lppQuery = new LppQuery();
        lppQuery.setOnCompleteListener((response, statusCode, success) -> {
            if (success) {
                log.setText("Data: " + response + "\nStatus code: " + statusCode);
            } else
                log.setText("Failed\nStatus code: " + statusCode);
        }).setParams(params).execute(LppQuery.GET_NEXT_STATION_FULL);

    }
}
