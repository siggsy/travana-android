package com.VegaSolutions.lpptransit.utility;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;

public class MapUtility {

    /**
     * Convert android drawable to bitmap suitable for google maps
     *
     * @param drawable drawable to be converted
     * @return bitmap created from drawable
     */
    public static BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    /**
     * Convert android drawable to bitmap suitable for google maps
     *
     * @param drawable drawable to be converted
     * @return bitmap created from drawable
     */
    public static BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable, int width, int height) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    /**
     * Calculate distance between two LatLng objects
     *
     * @param StartP start point
     * @param EndP   end point
     * @return distance in KM.
     */
    public static double calculationByDistance(LatLng StartP, LatLng EndP) {
        return calculationByDistance(StartP.latitude, StartP.longitude, EndP.latitude, EndP.longitude);
    }

    /**
     * Calculate distance between two LatLng objects
     *
     * @return distance in KM.
     */
    public static double calculationByDistance(double lat1, double lon1, double lat2, double lon2) {
        int Radius = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return Radius * c;
    }

    /**
     * Checks if location is permitted
     * @param context application context to be checked
     * @return boolean if location is permitted.
     */
    public static boolean checkLocationPermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkIfAtLeastOnePermissionPermitted(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static final int FINE_LOCATION = 0b10;
    public static final int COARSE_LOCATION = 0b01;

    public static boolean hasPermission(int permission, int ... required) {
        int mask = 0;
        for (int r : required)
            mask |= r;
        return (permission & mask) != 0;
    }

    /**
     * Get int representing location permission level
     * @param context with which to check the permission
     * @return 3 - FINE && COARSE, 2 - FINE, 1 - COARSE, 0 - NONE
     */
    public static int getGrantedLocationPermission(Context context) {
        if (checkLocationPermission(context))
            return FINE_LOCATION | COARSE_LOCATION;
        else if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            return FINE_LOCATION;
        else if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            return COARSE_LOCATION;
        else return 0;
    }

    public static LatLng getLatLngFromLocation(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

}
