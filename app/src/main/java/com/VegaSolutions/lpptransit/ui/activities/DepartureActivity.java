package com.VegaSolutions.lpptransit.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.ApiCallback;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.ApiResponse;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.TimetableWrapper;
import com.VegaSolutions.lpptransit.ui.Colors;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;

public class DepartureActivity extends AppCompatActivity {

    public static final String STATION_CODE = "station_code";
    public static final String STATION_NAME = "station_name";
    public static final String ROUTE_NUMBER = "route_number";
    public static final String ROUTE_NAME = "route_name";


    private String station_code;
    private String station_name;
    private String route_number;
    private String route_name;

    private TextView routeName, routeNumber, stationName, stationCenter;
    private View routeNumberCircle;
    private RecyclerView rv;
    private FrameLayout header;

    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_departure);

        station_code = getIntent().getStringExtra(STATION_CODE);
        station_name = getIntent().getStringExtra(STATION_NAME);
        route_number = getIntent().getStringExtra(ROUTE_NUMBER);
        route_name = getIntent().getStringExtra(ROUTE_NAME);

        routeName = findViewById(R.id.departure_route_name);
        routeNumber = findViewById(R.id.route_station_number);
        stationName = findViewById(R.id.departure_station_name);
        stationCenter = findViewById(R.id.station_center);
        routeNumberCircle = findViewById(R.id.include);
        rv = findViewById(R.id.departure_rv);
        header = findViewById(R.id.header);

        routeName.setText(route_name);
        routeNumber.setText(route_number);
        routeNumber.setTextSize(18f);
        stationName.setText(station_name);
        stationCenter.setVisibility(Integer.valueOf(station_code) % 2 != 0 ? View.VISIBLE : View.GONE);

        String group = route_number.replaceAll("[^0-9]", "");
        int color = Integer.valueOf(group);
        routeNumberCircle.getBackground().setTint(Colors.colors.get(color));

        adapter = new Adapter();

        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                header.setSelected(rv.canScrollVertically(-1));
            }
        });

        Api.timetable(Integer.valueOf(station_code), 100, 100, (apiResponse, statusCode, success) -> {
            if (success) {

                for (TimetableWrapper.RouteGroup.Route route : apiResponse.getData().getRoute_groups().get(0).getRoutes()) {
                    if (route.getParent_name().equals(route_name)) {
                        runOnUiThread(() -> adapter.setTimetables(route.getTimetable()));
                        return;
                    }
                }

            }
        }, Integer.valueOf(group));

    }

    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        List<TimetableWrapper.RouteGroup.Route.Timetable> timetables = new ArrayList<>();

        public void setTimetables(List<TimetableWrapper.RouteGroup.Route.Timetable> timetables) {
            this.timetables = timetables;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(getLayoutInflater().inflate(R.layout.template_departure_hour, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            TimetableWrapper.RouteGroup.Route.Timetable timetable = timetables.get(position);

            holder.hour.setText(String.valueOf(timetable.getHour()));
            holder.minutes.removeAllViews();

            for (int min : timetable.getMinutes()) {
                TextView textView = (TextView) getLayoutInflater().inflate(R.layout.template_departure_min, holder.minutes, false);
                textView.setText(String.valueOf(min));
                textView.setTextColor(timetable.isIs_current() ? Color.WHITE : Color.GRAY);
                holder.minutes.addView(textView);
            }

            if (timetable.isIs_current()) {
                holder.hour.setTextColor(Color.WHITE);
            }
            else {
                holder.hour.setTextColor(Color.BLACK);
            }

            holder.container.getBackground().setTint(timetable.isIs_current() ? ResourcesCompat.getColor(getResources(), R.color.colorAccent, null) : Color.WHITE);



        }

        @Override
        public int getItemCount() {
            return timetables.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            TextView hour;
            FlexboxLayout minutes;

            LinearLayout container;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                hour = itemView.findViewById(R.id.departure_hour_hour);
                minutes = itemView.findViewById(R.id.departure_hour_minutes);
                container = itemView.findViewById(R.id.departure_container);

            }
        }

    }

}
