package com.VegaSolutions.lpptransit.ui.custommaps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.VegaSolutions.lpptransit.R;
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

    private final IconGenerator mClusterIconGeneratorBig;
    private final IconGenerator mClusterIconGeneratorMed;
    private final IconGenerator mClusterIconGeneratorSml;
    final Drawable clusterIconBig;
    final Drawable clusterIconMed;
    final Drawable clusterIconSml;

    public CustomClusterRenderer(Context context, GoogleMap map, ClusterManager<StationMarker> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
        this.mClusterIconGeneratorBig = new IconGenerator(context);
        this.mClusterIconGeneratorMed = new IconGenerator(context);
        this.mClusterIconGeneratorSml = new IconGenerator(context);
        this.clusterIconBig = ContextCompat.getDrawable(context, R.drawable.circle_3);
        this.clusterIconMed = ContextCompat.getDrawable(context, R.drawable.circle_2);
        this.clusterIconSml = ContextCompat.getDrawable(context, R.drawable.circle);
        clusterIconBig.setColorFilter(context.getResources().getColor(R.color.main_blue), PorterDuff.Mode.SRC_ATOP);
        clusterIconMed.setColorFilter(context.getResources().getColor(R.color.color_main_orange), PorterDuff.Mode.SRC_ATOP);
        clusterIconSml.setColorFilter(context.getResources().getColor(R.color.main_green), PorterDuff.Mode.SRC_ATOP);
        setupIconGen(mClusterIconGeneratorBig, clusterIconBig, context);
        setupIconGen(mClusterIconGeneratorMed, clusterIconMed, context);
        setupIconGen(mClusterIconGeneratorSml, clusterIconSml, context);
    }

    private void setupIconGen(IconGenerator generator, Drawable drawable, Context context) {
        TextView textView = new TextView(context);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setTextColor(ContextCompat.getColor(context, R.color.white));
        textView.setId(com.google.maps.android.R.id.amu_text);
        textView.setGravity(android.view.Gravity.CENTER);
        textView.setLayoutParams(new FrameLayout.LayoutParams(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()));
        generator.setContentView(textView);
        generator.setBackground(drawable);
    }

    @Override
    protected String getClusterText(int bucket) {
        return super.getClusterText(bucket);
    }


    @Override
    protected void onBeforeClusterRendered(Cluster<StationMarker> cluster, MarkerOptions markerOptions) {

        if (cluster.getSize() > 20) {
            Bitmap icon = mClusterIconGeneratorBig.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        } else if (cluster.getSize() > 10) {
            Bitmap icon = mClusterIconGeneratorMed.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        } else {
            Bitmap icon = mClusterIconGeneratorSml.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }
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
