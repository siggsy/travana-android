package com.VegaSolutions.lpptransit

import android.app.Application
import com.VegaSolutions.lpptransit.data.TravanaRepository
import com.VegaSolutions.lpptransit.data.lppapi.NeoApi
import com.VegaSolutions.lpptransit.utility.NetworkConnectivityManager

class TravanaApp : Application() {

    lateinit var networkConnectivityManager: NetworkConnectivityManager
        private set

    lateinit var repository: TravanaRepository
        private set

    override fun onCreate() {
        super.onCreate()
        networkConnectivityManager = NetworkConnectivityManager(this)
        repository = TravanaRepository(context = this, api = NeoApi())
    }

}
