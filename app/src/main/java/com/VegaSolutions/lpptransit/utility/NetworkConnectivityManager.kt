package com.VegaSolutions.lpptransit.utility

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build

class NetworkConnectivityManager(context: Context) {
    var isConnectionAvailable: Boolean = false
        private set

    private val callbacks = ArrayList<NetworkCallback>()

    init {
        val networkCallback: NetworkCallback = object : NetworkCallback() {
            override fun onAvailable(network: Network) {
                isConnectionAvailable = true
                for (callback in callbacks) {
                    callback.onAvailable(network)
                }
            }

            override fun onLost(network: Network) {
                isConnectionAvailable = false
                for (callback in callbacks) {
                    callback.onLost(network)
                }
            }
        }

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        } else {
            val request =
                NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build()
            connectivityManager.registerNetworkCallback(request, networkCallback)
        }
    }

    companion object {
        const val NO_INTERNET_CONNECTION: Int = -1
        const val ERROR_DURING_LOADING: Int = -2
        const val NO_ERROR: Int = 0
    }
}
