package com.VegaSolutions.lpptransit.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.TravanaApp;
import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Route;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.SearchTryItem;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Station;
import com.VegaSolutions.lpptransit.utility.Colors;
import com.VegaSolutions.lpptransit.utility.NetworkConnectivityManager;
import com.VegaSolutions.lpptransit.utility.ScreenState;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.VegaSolutions.lpptransit.utility.ScreenState.DONE;
import static com.VegaSolutions.lpptransit.utility.ScreenState.ERROR;
import static com.VegaSolutions.lpptransit.utility.ScreenState.LOADING;

public class SearchActivity extends AppCompatActivity {

    public static final String TAG = "SearchActivity";

    // Activity UI elements
    RecyclerView searchList;
    ImageView back;
    SearchView searchView;
    FrameLayout header;
    ProgressBar progressBar;

    LinearLayout errorContainer;
    TextView errorText;
    ImageView errorImageView;
    TextView tryAgainText;

    // Search objects
    SearchAdapter adapter;
    String filter = "";
    final List<SearchItem> items = Collections.synchronizedList(new ArrayList<>());

    private TravanaApp app;
    private NetworkConnectivityManager networkConnectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        app = TravanaApp.getInstance();
        networkConnectivityManager = app.getNetworkConnectivityManager();

        initElements();
        setupUi(ERROR);

        retrieveStationsAndRoutes();
    }

    void retrieveStationsAndRoutes() {
        if (!networkConnectivityManager.isConnectionAvailable()) {
            setupUi(ERROR);
            setErrorUi(this.getResources().getString(R.string.no_internet_connection), R.drawable.ic_no_wifi);
            return;
        }
        setupUi(LOADING);


        // if in app there are valid stations data use them, otherwise retrieve new stations data
        if (app.areStationsLoaded()) {
            for (Station station : app.getStations())
                items.add(new StationItem(station));

            loadRoutes();
        } else {
            Api.stationDetails(true, (apiResponse, statusCode, success) -> {
                if (success) {
                    // Add stations
                    app.setStations((ArrayList) apiResponse.getData());
                    for (Station station : apiResponse.getData())
                        items.add(new StationItem(station));

                    loadRoutes();
                } else {
                    setupUi(ERROR);
                    setErrorUi(this.getResources().getString(R.string.error_loading), R.drawable.ic_error_outline);
                }
            });
        }
    }

    void loadRoutes() {
        Api.activeRoutes((apiResponse1, statusCode1, success1) -> {
            if (success1) {
                for (Route route : apiResponse1.getData())
                    items.add(new RouteItem(route));
                runOnUiThread(() -> applyFilter(filter));
                setupUi(DONE);
            } else {
                setupUi(ERROR);
                setErrorUi(this.getResources().getString(R.string.error_loading), R.drawable.ic_error_outline);
            }
        });
    }

    void setErrorUi(String errorName, int errorIconCode) {
        errorText.setText(errorName);
        errorImageView.setImageResource(errorIconCode);
    }

    void setupUi(ScreenState screenState) {
        runOnUiThread(() -> {
            switch (screenState) {
                case DONE: {
                    this.progressBar.setVisibility(View.GONE);
                    this.searchList.setVisibility(View.VISIBLE);
                    this.errorContainer.setVisibility(View.GONE);
                    break;
                }
                case LOADING: {
                    this.progressBar.setVisibility(View.VISIBLE);
                    this.searchList.setVisibility(View.GONE);
                    this.errorContainer.setVisibility(View.GONE);
                    break;
                }
                case ERROR: {
                    this.progressBar.setVisibility(View.GONE);
                    this.searchList.setVisibility(View.GONE);
                    this.errorContainer.setVisibility(View.VISIBLE);
                    break;
                }
            }
        });
    }

    void initElements() {

        // Find all UI elements
        searchList = findViewById(R.id.search_activity_rv);
        searchView = findViewById(R.id.search_activity_search);
        back = findViewById(R.id.search_activity_back);
        header = findViewById(R.id.search_activity_header);
        progressBar = findViewById(R.id.progressBar);
        errorText = findViewById(R.id.tv_error);
        errorImageView = findViewById(R.id.iv_error);
        tryAgainText = findViewById(R.id.tv_try_again);
        errorContainer = findViewById(R.id.ll_error_container);

        ImageView searchClose = searchView.findViewById(R.id.search_close_btn);
        searchClose.setColorFilter(ViewGroupUtils.isDarkTheme(this) ? Color.WHITE : Color.BLACK, android.graphics.PorterDuff.Mode.SRC_IN);

        back.setOnClickListener(view -> finish());
        tryAgainText.setOnClickListener(view -> retrieveStationsAndRoutes());

        // RV
        adapter = new SearchAdapter();
        searchList.setAdapter(adapter);
        searchList.setLayoutManager(new LinearLayoutManager(this));
        searchList.setHasFixedSize(false);
        searchList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                header.setSelected(recyclerView.canScrollVertically(-1));
            }
        });

        // Search interface
        searchView.requestFocusFromTouch();
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

    }

    void applyFilter(String text) {

        ArrayList<SearchItem> filteredItems = new ArrayList<>();

        if(text.isEmpty()) {
            //Load saved items
            List<SearchTryItem> searchItemsList = Api.getSavedSearchItemsIds(this);

            Collections.sort(searchItemsList,
                    (o1, o2) -> o1.getDate().compareTo(o2.getDate()));

            for (int i = searchItemsList.size() - 1; i >= 0; i--) {
                String searchItemId = searchItemsList.get(i).getSearchItemId();
                for (SearchItem item : this.items) {
                    if (item.getType() == SearchItem.STATION) {
                        StationItem stationItem = (StationItem) item;
                        if (stationItem.getStation().getRefId().equals(searchItemId)) {
                            filteredItems.add(item);
                        }
                    } else {
                        RouteItem routeItem = (RouteItem) item;
                        if (routeItem.getRoute().getTripId().equals(searchItemId)) {
                            filteredItems.add(item);
                        }
                    }
                }
            }
        } else {

            // Ignore all special Slovene characters
            text = text.toLowerCase().replace('č', 'c').replace('š', 's').replace('ž', 'z').trim();

            // Find an item and add to the list
            synchronized (this.items) {
                for (SearchItem item : this.items) {
                    String itemName = item.searchText.toLowerCase().replace('č', 'c').replace('š', 's').replace('ž', 'z').trim();
                    if (itemName.contains(text))
                        filteredItems.add(item);
                }
            }
        }
        adapter.setItems(filteredItems);
        this.runOnUiThread(() -> adapter.notifyDataSetChanged());

    }

    class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        ArrayList<SearchItem> items = new ArrayList<>();

        private void setItems(ArrayList<SearchItem> items) {
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

                // Update ViewHolder
                viewHolder.name.setText(stationItem.station.getName());
                viewHolder.image.setVisibility(View.VISIBLE);
                viewHolder.circle.setVisibility(View.GONE);
                viewHolder.center.setVisibility(Integer.parseInt(stationItem.station.getRefId()) % 2 != 0 ? View.VISIBLE : View.GONE);
                viewHolder.image.setImageResource((R.drawable.ic_location_pin));
                viewHolder.ll.setOnClickListener(v -> {
                    Intent i = new Intent(SearchActivity.this, StationActivity.class);
                    i.putExtra("station", stationItem.station);
                    startActivity(i);
                });

            } else if (item.getType() == SearchItem.ROUTE) {

                RouteItem routeItem = (RouteItem) item;

                // Update ViewHolder
                viewHolder.image.setVisibility(View.GONE);
                viewHolder.circle.setVisibility(View.VISIBLE);
                viewHolder.center.setVisibility(View.GONE);
                viewHolder.number.setText(routeItem.route.getRouteNumber());
                viewHolder.circle.findViewById(R.id.route_station_circle).getBackground().setTint(Colors.getColorFromString(routeItem.route.getRouteNumber()));
                viewHolder.name.setText(routeItem.route.getRouteName());
                viewHolder.ll.setOnClickListener(v -> {
                    Intent intent = new Intent(SearchActivity.this, RouteActivity.class);
                    intent.putExtra(RouteActivity.ROUTE_NUMBER, routeItem.route.getRouteNumber());
                    intent.putExtra(RouteActivity.ROUTE_NAME, routeItem.route.getRouteName());
                    intent.putExtra(RouteActivity.ROUTE_ID, routeItem.route.getRouteId());
                    intent.putExtra(RouteActivity.TRIP_ID, routeItem.route.getTripId());
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


    // Wrapper classes for RV adapter //
    abstract class SearchItem {

        static final int ROUTE = 0;
        static final int STATION = 1;
        String searchText;

        abstract int getType();

    }

    class StationItem extends SearchItem {

        Station station;

        StationItem(Station station) {
            this.station = station;
            searchText = station.getName();
        }

        @Override
        int getType() {
            return STATION;
        }

        public Station getStation() {
            return station;
        }
    }

    class RouteItem extends SearchItem {

        Route route;

        RouteItem(Route route) {
            this.route = route;
            searchText = route.getRouteNumber() + " " + route.getRouteName();
        }

        @Override
        int getType() {
            return ROUTE;
        }

        public Route getRoute() {
            return route;
        }
    }

}
