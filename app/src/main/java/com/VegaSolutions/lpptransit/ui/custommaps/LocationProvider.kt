package com.VegaSolutions.lpptransit.ui.custommaps

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationRequest
import android.util.Log
import com.VegaSolutions.lpptransit.utility.MapUtility
import com.VegaSolutions.lpptransit.utility.TAG
import kotlin.properties.Delegates

object LocationProvider {

    private const val MIN_METERS = 20f
    private const val MIN_MILLIS = 2 * 60 * 1000L

    private val listeners: MutableSet<LocationProviderListener> = HashSet()
    private var locationManager: LocationManager? = null

    var prevLocation: Location? by Delegates.observable(null) { _, old, new ->
        if (old != new && new != null)
            listeners.forEach { it.onLocationChanged(new) }
    }
        private set

    var isLive by Delegates.observable(false) { _, old, new ->
        if (old != new)
            listeners.forEach { it.onAvailabilityChanged(new) }
    }
        private set

    private val registeredProviders = HashSet<String>()
    private var fuse = false
        get() = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S
            && registeredProviders.contains(LocationManager.FUSED_PROVIDER)
            && field
    private var gps = false
        get() = registeredProviders.contains(LocationManager.GPS_PROVIDER) && field
    private var network = false
        get() = registeredProviders.contains(LocationManager.NETWORK_PROVIDER) && field

    private val mainListener = object : LocationListener {
        override fun onLocationChanged(newLocation: Location) {
            Log.i(this@LocationProvider.TAG, "New location: $newLocation")
            if (!isLive) {
                isLive = true
            }

            prevLocation = when(newLocation.provider) {
                // Optimal provider when sdk version 31
                LocationManager.FUSED_PROVIDER -> {
                    newLocation
                }

                // Legacy providers. Emulate fused
                else -> {
                    bestLocation(newLocation, prevLocation)
                }
            }
        }

        override fun onProviderDisabled(provider: String) {
            when (provider) {
                LocationManager.FUSED_PROVIDER -> fuse = false
                LocationManager.GPS_PROVIDER -> gps = false
                LocationManager.NETWORK_PROVIDER -> network = false
            }
            isLive = gps || network || fuse
        }

        override fun onProviderEnabled(provider: String) {
            when (provider) {
                LocationManager.FUSED_PROVIDER -> fuse = true
                LocationManager.GPS_PROVIDER -> gps = true
                LocationManager.NETWORK_PROVIDER -> network = true
            }
            isLive = gps || network || fuse
        }
    }

    @Synchronized
    fun subscribe(context: Context, locationListener: LocationProviderListener) {
        if (listeners.contains(locationListener))
            return

        listeners.add(locationListener)
        val lm = locationManager ?: context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager = lm

        if (listeners.size == 1)
            enable(lm, MapUtility.getGrantedLocationPermission(context))
    }

    @Synchronized
    fun unsubscribe(locationListener: LocationProviderListener) {
        listeners.remove(locationListener)
        if (listeners.isEmpty()) {
            Log.i(TAG, "Removing location updates")
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
        val useFUSE = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S

        if (useFUSE) {
            Log.i(TAG, "Enabling FUSE provider")
            lm.requestLocationUpdates(LocationManager.FUSED_PROVIDER, minTime, minDist, mainListener)
            prevLocation = bestLocation(lm.getLastKnownLocation(LocationManager.FUSED_PROVIDER), prevLocation)
            fuse = lm.isProviderEnabled(LocationManager.FUSED_PROVIDER)
            registeredProviders.add(LocationManager.FUSED_PROVIDER)
        } else {
            if (MapUtility.hasPermission(permissions, MapUtility.FINE_LOCATION)) {
                Log.i(TAG, "Enabling GPS provider")
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDist, mainListener)
                prevLocation = bestLocation(lm.getLastKnownLocation(LocationManager.GPS_PROVIDER), prevLocation)
                gps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
                registeredProviders.add(LocationManager.GPS_PROVIDER)
            }
            if (MapUtility.hasPermission(permissions, MapUtility.COARSE_LOCATION)) {
                Log.i(TAG, "Enabling NETWORK provider")
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDist, mainListener)
                prevLocation = bestLocation(lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER), prevLocation)
                network = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                registeredProviders.add(LocationManager.NETWORK_PROVIDER)
            }
        }

        isLive = network || gps || fuse
    }

}

interface LocationProviderListener {
    fun onLocationChanged(location: Location)
    fun onAvailabilityChanged(isLive: Boolean)
}