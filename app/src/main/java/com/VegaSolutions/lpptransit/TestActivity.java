package com.VegaSolutions.lpptransit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.ApiCallback;
import com.VegaSolutions.lpptransit.lppapi.LppQuery;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.ApiResponse;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.LiveBusArrival;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.LiveBusArrivalV2;

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
        }).addParams(params).execute(LppQuery.GET_ROUTE_DETAILS);
        */

        // Api test
        // getRouteDetails
        /*Api.getRouteDetails(341, (data, statusCode, success) -> {
            if (success) {
                log.setText(data.get(Api.RouteDetails.length) + "");
            }
        });*/

        // getRoutes
        /*Api.getRoutes_route_name("6", (data, statusCode, success) -> {
            if (success) {
                log.setText((String) data.get(0).get(Api.Routes.parent_name));
            }
        });*/

        /*Api.getRoutesOnStation_station_int_id(1934, (data, statusCode, success) -> {
            if (success) {
                log.setText(data.get(1));
            }
        });*/

        /*Api.getStationById((data, statusCode, success) -> {
            if (success) {
                log.setText(((List<Double>) data.get(0).get(Api.StationById.geometry)).get(0) + "");
            } else {
                log.setText("Failed" + statusCode);
            }
        });*/

        /*Api.stationsInRange(46.056319, 14.505381, (apiResponse, statusCode, success) -> {
            if (success) {
                log.setText(apiResponse.getData().get(0).getGeometry().getCoordinates()[0] + "");
            }
        });*/

        /*Api.liveBusArrival(1934, (apiResponse, statusCode, success) -> {
            if (success) {
                if (apiResponse.isSuccess()) {
                    log.setText(apiResponse.getData().get(0).getEta() + "");
                }
            }
        });*/
        Api.liveBusArrivalV2(1934, (apiResponse, statusCode, success) -> {
            if (success) {
                if (apiResponse.isSuccess()) {
                    log.setText(apiResponse.getData().getRoutes().get(0).getRoute_name());
                }
            }
        });
    }
}
