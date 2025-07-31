package com.VegaSolutions.lpptransit

import android.app.Application
import com.VegaSolutions.lpptransit.data.TravanaRepository
import com.VegaSolutions.lpptransit.data.lppapi.NeoApi
import com.VegaSolutions.lpptransit.utility.NetworkConnectivityManager

class TravanaApp : Application() {

    lateinit var repository: TravanaRepository
        private set

    lateinit var networkConnectivityManager: NetworkConnectivityManager
        private set

    override fun onCreate() {
        super.onCreate()

        repository = TravanaRepository(
            context = this,
            api = NeoApi()
        )
        networkConnectivityManager = NetworkConnectivityManager(this)
    }

}
