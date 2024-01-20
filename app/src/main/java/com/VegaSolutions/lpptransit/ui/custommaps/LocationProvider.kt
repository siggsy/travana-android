package com.VegaSolutions.lpptransit.ui.custommaps

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.util.Log
import com.VegaSolutions.lpptransit.utility.MapUtility
import com.VegaSolutions.lpptransit.utility.TAG

object LocationProvider {

    private const val MIN_METERS = 20f
    private const val MIN_MILLIS = 2 * 60 * 1000L

    private val listeners: MutableSet<LocationProviderListener> = HashSet()
    private var locationManager: LocationManager? = null

    var isLive: Boolean = false
        private set
    var prevLocation: Location? = null
        private set

    private val mainListener = locationListener@{ newLocation: Location ->
        Log.i(TAG, "New location: $newLocation")
        if (!isLive) {
            isLive = true
            listeners.forEach { it.onAvailabilityChanged(true) }
        }

        when(newLocation.provider) {

            // Optimal provider when sdk version 31
            LocationManager.FUSED_PROVIDER -> {
                listeners.forEach { it.onLocationChanged(newLocation) }
                prevLocation = newLocation
            }

            // Legacy providers. Emulate fused
            else -> {
                val prev = prevLocation
                prevLocation = bestLocation(prevLocation, newLocation)

                // Notify if changed
                if (prev != prevLocation) {
                    listeners.forEach { it.onLocationChanged(prevLocation ?: newLocation) }
                }
            }
        }
        return@locationListener
    }

    @Synchronized
    fun subscribe(context: Context, locationListener: LocationProviderListener) {
        listeners.add(locationListener)

        val lm = locationManager ?: context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager = lm

        if (listeners.size == 1)
            enable(lm, MapUtility.getGrantedLocationPermission(context))

        prevLocation?.let {
            locationListener.onAvailabilityChanged(isLive)
            locationListener.onLocationChanged(it)
        }
    }

    @Synchronized
    fun unsubscribe(locationListener: LocationProviderListener) {
        listeners.remove(locationListener)
        if (listeners.isEmpty()) {
            locationManager?.removeUpdates(mainListener)
            locationManager = null
        }
    }

    private fun bestLocation(a: Location?, b: Location?): Location? {
        if (a == null)
            return b
        if (b == null)
            return a

        val timeDelta      = a.time - b.time
        val accuracyRatio  = a.accuracy / b.accuracy
        val isProviderSame = a.provider == b.provider

        return when {
            // Big time difference, choose most recent
            timeDelta > MIN_MILLIS  -> a
            timeDelta < -MIN_MILLIS -> b

            // Choose based on accuracy
            timeDelta > 0 && accuracyRatio <= 1                     -> a
            timeDelta > 0 && accuracyRatio <= 1.4 && isProviderSame -> a
            else                                                    -> b
        }
    }

    @SuppressLint("MissingPermission")
    private fun enable(lm: LocationManager, permissions: Int, minTime: Long = MIN_MILLIS, minDist: Float = MIN_METERS) {
        if (permissions == 0) return

        val hasGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val hasNETWORK = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        val hasFUSE =
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S &&
            lm.isProviderEnabled(LocationManager.FUSED_PROVIDER)

        if (hasFUSE) {
            lm.requestLocationUpdates(LocationManager.FUSED_PROVIDER, minTime, minDist, mainListener)
            prevLocation = lm.getLastKnownLocation(LocationManager.FUSED_PROVIDER)
        } else {
            if (hasGPS && MapUtility.hasPermission(permissions, MapUtility.FINE_LOCATION)) {
                Log.i(TAG, "Enabling GPS provider")
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDist, mainListener)
                prevLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            }
            if (hasNETWORK && MapUtility.hasPermission(permissions, MapUtility.COARSE_LOCATION)) {
                Log.i(TAG, "Enabling NETWORK provider")
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDist, mainListener)
                prevLocation = bestLocation(prevLocation, lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER))
            }
        }
    }

}

interface LocationProviderListener {
    fun onLocationChanged(location: Location)
    fun onAvailabilityChanged(isLive: Boolean)
}