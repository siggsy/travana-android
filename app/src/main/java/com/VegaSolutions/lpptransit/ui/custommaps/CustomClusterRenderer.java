package com.VegaSolutions.lpptransit.ui.custommaps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.VegaSolutions.lpptransit.R;
import com.VegaSolutions.lpptransit.utility.Colors;
import com.VegaSolutions.lpptransit.utility.MapUtility;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

public class CustomClusterRenderer extends DefaultClusterRenderer<StationMarker> {

    private final Context context;
    private final IconGenerator mClusterIconGenerator;

    public CustomClusterRenderer(Context context, GoogleMap map, ClusterManager<StationMarker> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
        this.mClusterIconGenerator = new IconGenerator(context);
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
    protected void onBeforeClusterRendered(Cluster<StationMarker> cluster, MarkerOptions markerOptions) {

        final Drawable clusterIcon = context.getResources().getDrawable(R.drawable.circle);
        clusterIcon.setColorFilter(Colors.colors.get(cluster.getSize() % 71), PorterDuff.Mode.SRC_ATOP);

        mClusterIconGenerator.setBackground(clusterIcon);

        //modify padding for one or two digit numbers
        if (cluster.getSize() < 10) {
            mClusterIconGenerator.setContentPadding(40, 20, 0, 0);
        } else {
            mClusterIconGenerator.setContentPadding(30, 20, 0, 0);
        }

        Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }

    @Override
    protected void onBeforeClusterItemRendered(StationMarker item, MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions.icon(MapUtility.getMarkerIconFromDrawable(ContextCompat.getDrawable(context, R.drawable.station_circle))).anchor(0.4f, 0.4f));
    }

    @Override
    protected void onClusterItemRendered(StationMarker clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
        marker.setTag(clusterItem.getStation());
    }
}
