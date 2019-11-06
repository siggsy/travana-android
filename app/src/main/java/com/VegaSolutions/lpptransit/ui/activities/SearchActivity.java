package com.VegaSolutions.lpptransit.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Route;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Station;
import com.VegaSolutions.lpptransit.ui.Colors;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    RecyclerView searchList;
    ImageView back;
    SearchView searchView;
    FrameLayout header;

    SearchAdapter adapter;
    private String filter = "";

    List<SearchItem> items = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchList = findViewById(R.id.search_activity_rv);
        searchView = findViewById(R.id.search_activity_search);
        back = findViewById(R.id.search_activity_back);
        header = findViewById(R.id.search_activity_header);

        adapter = new SearchAdapter();

        searchList.setAdapter(adapter);
        searchList.setLayoutManager(new LinearLayoutManager(this));
        searchList.setHasFixedSize(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                applyFilter(query);
                filter = query;
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                applyFilter(newText);
                filter = newText;
                return true;
            }
        });

        searchList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                header.setSelected(recyclerView.canScrollVertically(-1));
            }
        });

        back.setOnClickListener(view -> finish());

        Api.stationDetails(true, (apiResponse, statusCode, success) -> {
            if (success) {
                for (Station station : apiResponse.getData())
                    items.add(new StationItem(station));
            }
            Api.activeRoutes((apiResponse1, statusCode1, success1) -> {
                if (success1) {
                    for (Route route : apiResponse1.getData())
                        items.add(new RouteItem(route));
                    runOnUiThread(() -> applyFilter(filter));
                }
            });
        });





    }

    void applyFilter(String text) {

        if(text.isEmpty()){
            adapter.items.clear();
        } else {
            ArrayList<SearchItem> items = new ArrayList<>();
            text = text.toLowerCase().replace('č', 'c').replace('š', 's').replace('ž', 'z');
            for(SearchItem item : this.items) {
                String itemName = item.searchText.toLowerCase().replace('č', 'c').replace('š', 's').replace('ž', 'z');
                if (itemName.contains(text))
                    items.add(item);
            }
            adapter.setItems(items);
        }
        adapter.notifyDataSetChanged();

    }


    class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        ArrayList<SearchItem> items = new ArrayList<>();

        public void setItems(ArrayList<SearchItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(getLayoutInflater().inflate(R.layout.template_search_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            SearchItem item = items.get(position);
            ViewHolder viewHolder = (ViewHolder) holder;

            if (item.getType() == SearchItem.STATION) {

                StationItem stationItem = (StationItem) item;
                viewHolder.name.setText(stationItem.station.getName());

                viewHolder.image.setVisibility(View.VISIBLE);
                viewHolder.circle.setVisibility(View.GONE);
                viewHolder.center.setVisibility(Integer.valueOf(stationItem.station.getRef_id()) % 2 != 0 ? View.VISIBLE : View.GONE);
                viewHolder.image.setImageResource((R.drawable.ic_location_on_black_24dp));




                viewHolder.ll.setOnClickListener(v -> {
                    Intent i = new Intent(SearchActivity.this, StationActivity.class);
                    i.putExtra("station_code", stationItem.station.getRef_id());
                    i.putExtra("station_name", stationItem.station.getName());
                    i.putExtra("station_center", Integer.valueOf(stationItem.station.getRef_id()) % 2 != 0);
                    startActivity(i);
                });

            } else if (item.getType() == SearchItem.ROUTE) {
                RouteItem routeItem = (RouteItem) item;


                viewHolder.image.setVisibility(View.GONE);
                viewHolder.circle.setVisibility(View.VISIBLE);
                viewHolder.center.setVisibility(View.GONE);

                viewHolder.number.setText(routeItem.route.getRoute_number());

                String group = routeItem.route.getRoute_number().replaceAll("[^0-9]", "");
                int color = Integer.valueOf(group);
                viewHolder.circle.findViewById(R.id.route_station_circle).getBackground().setTint(Colors.colors.get(color));

                viewHolder.name.setText(routeItem.route.getRoute_name());
                viewHolder.ll.setOnClickListener(v -> {
                    Intent intent = new Intent(SearchActivity.this, RouteActivity.class);
                    intent.putExtra(RouteActivity.ROUTE_NUMBER, routeItem.route.getRoute_number());
                    intent.putExtra(RouteActivity.ROUTE_NAME, routeItem.route.getRoute_name());
                    intent.putExtra(RouteActivity.ROUTE_ID, routeItem.route.getRoute_id());
                    intent.putExtra(RouteActivity.TRIP_ID, routeItem.route.getTrip_id());
                    startActivity(intent);
                });

            }

        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            ImageView image;
            View circle;
            TextView name, number, center;
            LinearLayout ll;

            private ViewHolder(@NonNull View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.search_item_type);
                name = itemView.findViewById(R.id.search_item_name);
                ll = itemView.findViewById(R.id.search_item_ll);
                circle = itemView.findViewById(R.id.route_station_circle);
                number = itemView.findViewById(R.id.route_station_number);
                center = itemView.findViewById(R.id.station_center);
            }
        }


    }


    abstract class SearchItem {

        public static final int ROUTE = 0;
        public static final int STATION = 1;
        String searchText;

        abstract int getType();

    }

    class StationItem extends SearchItem {

        Station station;

        public StationItem(Station station) {
            this.station = station;
            searchText = station.getName();
        }

        @Override
        int getType() {
            return STATION;
        }
    }

    class RouteItem extends SearchItem {

        Route route;


        public RouteItem(Route route) {
            this.route = route;
            searchText = route.getRoute_number() + " " + route.getRoute_name();
        }

        @Override
        int getType() {
            return ROUTE;
        }
    }




}
