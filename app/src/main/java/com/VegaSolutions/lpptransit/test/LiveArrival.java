package com.VegaSolutions.lpptransit.test;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.ApiCallback;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.ApiResponse;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.ArrivalWrapper;
import com.VegaSolutions.lpptransit.lppapideprecated.responseclasses.LiveBusArrivalV2;

public class LiveArrival extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_arrival);

        LinearLayout linearLayout = findViewById(R.id.arrivals);

        String id = getIntent().getStringExtra("station_id");


        if (id != null) {
            Api.arrival(id, (apiResponse, statusCode, success) -> {
                if (success) {
                    ArrivalWrapper liveBusArrivalV2 = apiResponse.getData();
                    for (ArrivalWrapper.Arrival arrival : liveBusArrivalV2.getArrivals()) {
                        View v = getLayoutInflater().inflate(R.layout.adapter_live_arrivals, linearLayout, false);
                        TextView num = v.findViewById(R.id.live_arrival_number);
                        TextView name = v.findViewById(R.id.live_arrival_name);
                        TextView etc = v.findViewById(R.id.live_arrival_minutes);
                        v.setOnClickListener(view -> {
                            Intent i = new Intent(LiveArrival.this, MapsTestActivity.class);
                            i.putExtra("trip_id", arrival.getTrip_id());
                            startActivity(i);
                        });

                        num.setText(arrival.getRoute_name());
                        name.setText(arrival.getTrip_name());
                        etc.setText(arrival.getEta_min() + " min");

                        runOnUiThread(() -> linearLayout.addView(v));


                    }
                }
            });
        }
    }
}
