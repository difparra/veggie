package com.diegoparra.veggie.core

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_VALIDATED
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import timber.log.Timber

@RequiresApi(Build.VERSION_CODES.M)
class NetworkCallbackApi23(
    private val connectivityManager: ConnectivityManager,
    private val onNetworkVerified: (Network) -> Unit,
    private val onNetworkLost: (Network) -> Unit,
    private val onNetworksUnavailable: () -> Unit
): ConnectivityManager.NetworkCallback() {
    companion object {
        fun getNetworkRequest(): NetworkRequest =
            NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addCapability(NET_CAPABILITY_VALIDATED)
                .build()
    }


    private val buildVersionO = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    private fun checkInternetAvailable(
        network: Network,
        networkCapabilities: NetworkCapabilities
    ) {
        if (networkCapabilities.hasCapability(NET_CAPABILITY_VALIDATED)) {
            onNetworkVerified(network)
        } else {
            onNetworkLost(network)
        }
    }

    override fun onAvailable(network: Network) {
        Timber.d("onAvailable() called with: network = $network")
        if (!buildVersionO) {
            connectivityManager.getNetworkCapabilities(network)?.let {
                checkInternetAvailable(network, it)
            }
        }
    }

    override fun onCapabilitiesChanged(
        network: Network, networkCapabilities: NetworkCapabilities
    ) {
        Timber.d("onCapabilitiesChanged() called with: network = $network, networkCapabilities = $networkCapabilities")
        if (buildVersionO) {
            checkInternetAvailable(network, networkCapabilities)
        }
    }


    override fun onLost(network: Network) {
        Timber.d("onLost() called with: network = $network")
        onNetworkLost(network)
    }

    override fun onUnavailable() {
        Timber.d("onUnavailable() called")
        onNetworksUnavailable()
    }

}