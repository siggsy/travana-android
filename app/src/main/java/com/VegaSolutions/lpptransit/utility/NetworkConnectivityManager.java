package com.VegaSolutions.lpptransit.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class NetworkConnectivityManager {

    public static final int NO_INTERNET_CONNECTION = -1;
    public static final int ERROR_DURING_LOADING = -2;
    public static final int NO_ERROR = 0;

    private final Context context;
    private boolean isConnectionAvailable = false;
    private final ArrayList<ConnectivityManager.NetworkCallback> callbacks = new ArrayList<>();

    public NetworkConnectivityManager(Context context) {
        this.context = context;

        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                isConnectionAvailable = true;
                for (ConnectivityManager.NetworkCallback callback : callbacks) {
                    callback.onAvailable(network);
                }
            }

            @Override
            public void onLost(@NonNull Network network) {
                isConnectionAvailable = false;
                for (ConnectivityManager.NetworkCallback callback : callbacks) {
                    callback.onLost(network);
                }
            }
        };

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback);
        } else {
            NetworkRequest request = new NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build();
            connectivityManager.registerNetworkCallback(request, networkCallback);
        }
    }

    public boolean isConnectionAvailable() {
        return isConnectionAvailable;
    }
}
