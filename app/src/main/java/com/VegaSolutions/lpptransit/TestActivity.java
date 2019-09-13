package com.VegaSolutions.lpptransit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.ApiCallback;
import com.VegaSolutions.lpptransit.lppapi.LppQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // Set log TextView

        TextView log = findViewById(R.id.test_log_tv);
        log.setMovementMethod(new ScrollingMovementMethod());

        /*// Query test
        Map<String, String> params = new HashMap<>();
        params.put("route_int_id", "341");

        LppQuery lppQuery = new LppQuery();
        lppQuery.setOnCompleteListener((response, statusCode, success) -> {
            if (success) {
                log.setText("Data: " + response + "\nStatus code: " + statusCode);
            } else
                log.setText("Failed\nStatus code: " + statusCode);
        }).setParams(params).execute(LppQuery.GET_ROUTE_DETAILS);
        */

        // Api test

        Api.getRouteDetails(341, (data, statusCode, success) -> {
            if (success) {
                log.setText((Double) data.get(Api.RouteDetails.length) + "");
            }
        });

    }
}
