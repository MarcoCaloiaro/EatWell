package com.marcocaloiaro.eatwell.mapscreen.utils

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build

@Suppress("DEPRECATION")
class NetworkUtils private constructor() {

    companion object {

        fun isConnectionEnabled(connectivityManager: ConnectivityManager): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network: Network? = connectivityManager.activeNetwork
                val capabilities: NetworkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
                return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || capabilities.hasTransport(
                    NetworkCapabilities.TRANSPORT_WIFI
                )
            } else {
                val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                return when (activeNetwork?.type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    else -> false
                }
            }
        }
    }
}