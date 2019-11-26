package com.VegaSolutions.lpptransit.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.TimetableWrapper;
import com.VegaSolutions.lpptransit.utility.Colors;
import com.VegaSolutions.lpptransit.ui.errorhandlers.CustomToast;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;

public class DepartureActivity extends AppCompatActivity {

    public static final String STATION_CODE = "station_code";
    public static final String STATION_NAME = "station_name";
    public static final String ROUTE_NUMBER = "route_number";
    public static final String ROUTE_NAME = "route_name";

    // Activity parameters
    private String station_code;
    private String station_name;
    private String route_number;
    private String route_name;

    // Activity UI elements
    private TextView routeName, routeNumber, stationName, stationCenter;
    private View routeNumberCircle;
    private RecyclerView rv;
    private FrameLayout header;
    private View depErr;
    private ProgressBar progressBar;

    private Adapter adapter;

    // Private local variables
    private int textColor;
    private int backGroundColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ViewGroupUtils.isDarkTheme(this) ? R.style.DarkTheme : R.style.WhiteTheme);
        setContentView(R.layout.activity_departure);

        // Get theme default colors
        int[] attribute = new int[] { android.R.attr.textColor, R.attr.backgroundViewColor };
        TypedArray array = obtainStyledAttributes(ViewGroupUtils.isDarkTheme(this) ? R.style.DarkTheme : R.style.WhiteTheme, attribute);
        textColor = array.getColor(0, Color.BLACK);
        backGroundColor = array.getColor(1, Color.WHITE);
        array.recycle();

        // Get activity parameters
        station_code = getIntent().getStringExtra(STATION_CODE);
        station_name = getIntent().getStringExtra(STATION_NAME);
        route_number = getIntent().getStringExtra(ROUTE_NUMBER);
        route_name = getIntent().getStringExtra(ROUTE_NAME);


        // Find all UI elements
        routeName = findViewById(R.id.departure_route_name);
        routeNumber = findViewById(R.id.route_station_number);
        stationName = findViewById(R.id.departure_station_name);
        stationCenter = findViewById(R.id.station_center);
        routeNumberCircle = findViewById(R.id.include);
        rv = findViewById(R.id.departure_rv);
        header = findViewById(R.id.header);
        depErr = findViewById(R.id.departure_no_departures_error);
        progressBar = findViewById(R.id.progressBar);


        // Set UI elements
        routeName.setText(route_name);
        routeNumber.setText(route_number);
        routeNumber.setTextSize(16f);
        stationName.setText(station_name);
        stationCenter.setVisibility(Integer.valueOf(station_code) % 2 != 0 ? View.VISIBLE : View.GONE);
        routeNumberCircle.getBackground().setTint(Colors.getColorFromString(route_number));

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


        // Get group number
        String group = route_number.replaceAll("[^0-9]", "");

        Api.timetable(Integer.valueOf(station_code), 100, 100, (apiResponse, statusCode, success) -> {
            runOnUiThread(() -> {

                // Remove progress bar
                progressBar.setVisibility(View.GONE);
                if (success) {
                    TimetableWrapper timetableWrapper = apiResponse.getData();

                    // Notify user if timetable is empty
                    if (timetableWrapper.getRoute_groups().get(0).getRoutes().isEmpty())
                        depErr.setVisibility(View.VISIBLE);

                    // Search for the right timetable
                    for (TimetableWrapper.RouteGroup.Route route : timetableWrapper.getRoute_groups().get(0).getRoutes()) {
                        if (route.getParent_name().equals(route_name)) {
                            adapter.setTimetables(route.getTimetable());
                            // Notify user if empty
                            if (adapter.timetables.isEmpty())
                                depErr.setVisibility(View.VISIBLE);
                            return;
                        }
                    }

                    // If the right timetable was not found
                    depErr.setVisibility(View.VISIBLE);
                }

                // In case of error, show Toast with error message
                else new CustomToast(this).showDefault(this, statusCode);

            });
        }, Integer.valueOf(group));

    }

    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        List<TimetableWrapper.RouteGroup.Route.Timetable> timetables = new ArrayList<>();

        private void setTimetables(List<TimetableWrapper.RouteGroup.Route.Timetable> timetables) {
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

            // Set hour number
            holder.hour.setText(String.valueOf(timetable.getHour()));
            holder.minutes.removeAllViews();

            // Set minutes in an hour
            for (int min : timetable.getMinutes()) {
                TextView textView = (TextView) getLayoutInflater().inflate(R.layout.template_departure_min, holder.minutes, false);
                textView.setText(String.valueOf(min));
                textView.setTextColor(timetable.isCurrent() ? Color.WHITE : textColor);
                holder.minutes.addView(textView);
            }

            if (timetable.isCurrent()) {
                holder.hour.setTextColor(textColor);
                holder.container.getBackground().setTint(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
            } else {
                holder.hour.setTextColor(textColor);
                holder.container.getBackground().setTint(backGroundColor);
            }

        }

        @Override
        public int getItemCount() {
            return timetables.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            TextView hour;
            FlexboxLayout minutes;
            CardView container;

            private ViewHolder(@NonNull View itemView) {
                super(itemView);

                hour = itemView.findViewById(R.id.departure_hour_hour);
                minutes = itemView.findViewById(R.id.departure_hour_minutes);
                container = itemView.findViewById(R.id.departure_root);

            }
        }

    }

}
