package com.VegaSolutions.lpptransit.ui.custommaps;

import android.content.Context;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.ui.Colors;
import com.VegaSolutions.lpptransit.utility.MapUtility;
import com.VegaSolutions.lpptransit.utility.ViewGroupUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class CustomClusterRenderer extends DefaultClusterRenderer<StationMarker> {

    private Context context;

    public CustomClusterRenderer(Context context, GoogleMap map, ClusterManager<StationMarker> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
    }

    @Override
    protected int getColor(int clusterSize) {
        return Colors.colors.get(clusterSize % 71);
    }

    @Override
    protected String getClusterText(int bucket) {
        return super.getClusterText(bucket);
    }

    @Override
    protected void onBeforeClusterItemRendered(StationMarker item, MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions.icon(MapUtility.getMarkerIconFromDrawable(ContextCompat.getDrawable(context, R.drawable.station_circle))).anchor(0.5f, 0.5f));
    }

    @Override
    protected void onClusterItemRendered(StationMarker clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
        marker.setTag(clusterItem.getStation());
    }
}
