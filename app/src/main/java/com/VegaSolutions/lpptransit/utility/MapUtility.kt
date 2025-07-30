package com.VegaSolutions.lpptransit.utility

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import androidx.core.graphics.createBitmap

/**
 * Convert android drawable to bitmap suitable for google maps
 *
 * @param drawable drawable to be converted
 * @return bitmap created from drawable
 */
fun getMarkerIconFromDrawable(drawable: Drawable): BitmapDescriptor {
    val canvas = Canvas()
    val bitmap = createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight)
    canvas.setBitmap(bitmap)
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

/**
 * Convert android drawable to bitmap suitable for google maps
 *
 * @param drawable drawable to be converted
 * @return bitmap created from drawable
 */
fun getMarkerIconFromDrawable(drawable: Drawable, width: Int, height: Int): BitmapDescriptor {
    val canvas = Canvas()
    val bitmap = createBitmap(width, height)
    canvas.setBitmap(bitmap)
    drawable.setBounds(0, 0, width, height)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

/**
 * Calculate distance between two LatLng objects
 *
 * @param StartP start point
 * @param EndP   end point
 * @return distance in KM.
 */
fun calculationByDistance(StartP: LatLng, EndP: LatLng): Double {
    return calculationByDistance(
        StartP.latitude,
        StartP.longitude,
        EndP.latitude,
        EndP.longitude
    )
}

/**
 * Calculate distance between two LatLng objects
 *
 * @return distance in KM.
 */
fun calculationByDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val Radius = 6371
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = (sin(dLat / 2) * sin(dLat / 2)
            + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2) * sin(dLon / 2))
    val c = 2 * asin(sqrt(a))
    return Radius * c
}

/**
 * Checks if location is permitted
 * @param context application context to be checked
 * @return boolean if location is permitted.
 */
fun checkLocationPermission(context: Context): Boolean {
    return ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

fun checkIfAtLeastOnePermissionPermitted(context: Context): Boolean {
    return ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

const val FINE_LOCATION: Int = 2
const val COARSE_LOCATION: Int = 1

fun hasPermission(permission: Int, vararg required: Int): Boolean {
    var mask = 0
    for (r in required) mask = mask or r
    return (permission and mask) != 0
}

/**
 * Get int representing location permission level
 * @param context with which to check the permission
 * @return 3 - FINE && COARSE, 2 - FINE, 1 - COARSE, 0 - NONE
 */
fun getGrantedLocationPermission(context: Context): Int {
    return if (checkLocationPermission(context)) FINE_LOCATION or COARSE_LOCATION
    else if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) FINE_LOCATION
    else if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) COARSE_LOCATION
    else 0
}

fun getLatLngFromLocation(location: Location?): LatLng? {
    if (location == null) return null
    return LatLng(location.latitude, location.longitude)
}
