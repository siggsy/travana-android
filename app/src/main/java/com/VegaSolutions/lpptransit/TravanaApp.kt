package com.VegaSolutions.lpptransit

import android.app.Application
import com.VegaSolutions.lpptransit.data.TravanaRepository
import com.VegaSolutions.lpptransit.data.lppapi.NeoApi
import com.VegaSolutions.lpptransit.utility.NetworkConnectivityManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlin.coroutines.coroutineContext

class TravanaApp : Application() {

    lateinit var repository: TravanaRepository
        private set

    lateinit var networkConnectivityManager: NetworkConnectivityManager
        private set

    override fun onCreate() {
        super.onCreate()

        repository = TravanaRepository(
            context = this,
            api = NeoApi(),
            scope = CoroutineScope(Dispatchers.IO)
        )
        networkConnectivityManager = NetworkConnectivityManager(this)
    }

}
