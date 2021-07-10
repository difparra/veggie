package com.diegoparra.veggie.core

import android.net.*
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URL


//  WalledGarden = CaptivePortal
private const val mWalledGardenUrl = "http://clients3.google.com/generate_204"
private const val WALLED_GARDEN_SOCKET_TIMEOUT_MS = 100000

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class NetworkCallbackApi21(
    private val connectivityManager: ConnectivityManager,
    private val onNetworkVerified: (Network) -> Unit,
    private val onNetworkLost: (Network) -> Unit,
    private val onNetworksUnavailable: () -> Unit
): ConnectivityManager.NetworkCallback() {
    companion object {
        fun getNetworkRequest(): NetworkRequest =
            NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
    }


    private fun verifyInternetAccess(): Boolean {
        return isInternetAvailable() && !isWalledGardenConnection()
    }

    private fun isInternetAvailable(): Boolean {
        return try {
            val socket = Socket()
            socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
            socket.close()
            Timber.d("isInternetAvailable = true")
            true
        } catch (e: IOException) {
            Timber.d("No internet connection. $e")
            false
        }
    }

    /*  TODO: May be necessary to test this code on api lower than 23 and above 21.

        It may be necessary to add network configuration to manifest:
          android:usesCleartextTraffic="true"
        as the url to check captivePortal "http://clients3.google.com/generate_204" is not https.

        Another option would be by adding a network_security_config.xml in res/xml,
            <?xml version="1.0" encoding="utf-8"?>
            <network-security-config>
                <domain-config cleartextTrafficPermitted="true">
                    <domain includeSubdomains="true">clients3.google.com</domain>
                </domain-config>
            </network-security-config>
        and then android:networkSecurityConfig="@xml/network_security_config" to manifest.

        ** Check exceptions, if it is working, it should not throw an exception, just true or false
        from the try method.

        ** With this configuration, it is working in api 30.

        isWalledGardenConnection method taken from:
        https://stackoverflow.com/questions/13958614/how-to-check-for-unrestricted-internet-access-captive-portal-detection
    */
    private fun isWalledGardenConnection(): Boolean {
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL(mWalledGardenUrl)
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.instanceFollowRedirects = false
            urlConnection.connectTimeout = WALLED_GARDEN_SOCKET_TIMEOUT_MS
            urlConnection.readTimeout = WALLED_GARDEN_SOCKET_TIMEOUT_MS
            urlConnection.useCaches = false
            urlConnection.inputStream
            val isCaptivePortal = urlConnection.responseCode != 204
            Timber.d("isCaptivePortal = $isCaptivePortal")
            return isCaptivePortal
        } catch (e: IOException) {
            Timber.d("WalledGarden/CaptivePortal exception - probably not a portal: exception $e")
            return false
        } finally {
            urlConnection?.disconnect()
        }
    }


    //      ----------------------------------------------------------------------------------------

    override fun onAvailable(network: Network) {
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        val hasInternetCapability = networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        Timber.d("onAvailable() - network: $network, internetCapability = $hasInternetCapability")
        if (hasInternetCapability == true) {
            // check if this network actually has internet
            CoroutineScope(Dispatchers.IO).launch {
                val hasInternet = verifyInternetAccess()
                if (hasInternet) {
                    withContext(Dispatchers.Main) {
                        Timber.d("onAvailable: adding network. $network")
                        onNetworkVerified(network)
                    }
                }
            }
        }
    }

    override fun onLost(network: Network) {
        Timber.d("onLost() called with: network = $network")
        onNetworkLost(network)
    }

    override fun onUnavailable() {
        //  This method is not being called on Samsumg mobiles.
        Timber.d("onUnavailable() called")
        onNetworksUnavailable()
    }

}