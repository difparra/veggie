package com.diegoparra.veggie.core.internet_check

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.*
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkCapabilities.NET_CAPABILITY_VALIDATED
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

/**
 * Save all available networks with an internet connection to a set (@validNetworks).
 * As long as the size of the set > 0, this LiveData emits true.
 *
 * Created this class from:
 *  https://github.com/mitchtabian/food2fork-compose/blob/master/app/src/main/java/com/codingwithmitch/food2forkcompose/presentation/util/ConnectionLiveData.kt
 *
 *  I have made some changes trying to improve the functionality but it has still some issues to
 *  pay attention to:
 *
 *  - This live data will always have false as initial value, even if it has internet.
 *    However, if that is the case, the liveData will be rapidly updated to true.
 *    Reason:   This was because onUnavailable() in NetworkCallback is not always called (maybe due
 *              to compatibility issues) and when I tested on my phone, if I started the app without
 *              connected wifi or cellular data, liveData won't emit there is no internet.
 *  - Offline status will be almost completely reliable, but I can't say the same for online status.
 *      Offline status will be reliable because it will be reached when neither wifi nor data are
 *      connected or when internet access has been tested with socket. But it can still have some
 *      compatibility issues that can affect the callbacks.
 *      Online status can't always be reliable as:
 *          On the one hand there is not a method to test internet access that can work in all
 *          devices (NET_CAPABILITY_VALIDATED, sockets and pings are some methods but apparently
 *          none of them work in all devices and apis and all the time).
 *          On the other hand, internet access can't be validated real-time, it is called when some
 *          change in on/off connection status has changed.
 *  - Method used to detect if there is actually internet access:
 *    - APIs greater than 23 will use the NET_CAPABILITY_VALIDATED provided by android.
 *      When testing on my device, this was the only method that worked to prove actual
 *      internet connection.
 *    - APIs older than 23 will use socket connection to google in order to test if connection is
 *      actually working.
 *          According to some research this was the better and most effective method.
 *          It has not really worked in my device with cellular data on but not working, but is the
 *          best option I have found for apis older than 23.
 *
 *
 */
class ConnectionLiveData(context: Context) : LiveData<Boolean>() {

    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    private val validNetworks: MutableSet<Network> = HashSet()

    private fun checkValidNetworks() {
        postValue(validNetworks.size > 0)
    }

    override fun setValue(value: Boolean?) {
        super.setValue(value)
    }

    override fun onActive() {
        Timber.d("onActive() called")

        //  As onUnavailable method in NetworkCallback is not working, I should set false as init
        //  value. So that, when user enters the app and there is no internet connection value will
        //  be false.
        postValue(false)

        //  Creating and registering callback to listen to network changes.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            networkCallback = createNetworkCallbackApi23()
            val networkRequest = NetworkRequest.Builder()
                .addCapability(NET_CAPABILITY_INTERNET)
                .addCapability(NET_CAPABILITY_VALIDATED)
                .build()
            cm.registerNetworkCallback(networkRequest, networkCallback)
        } else {
            networkCallback = createNetworkCallback()
            val networkRequest = NetworkRequest.Builder()
                .addCapability(NET_CAPABILITY_INTERNET)
                .build()
            cm.registerNetworkCallback(networkRequest, networkCallback)
        }
    }

    override fun onInactive() {
        Timber.d("onInactive() called")
        cm.unregisterNetworkCallback(networkCallback)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun createNetworkCallbackApi23() = object : ConnectivityManager.NetworkCallback() {
        private val buildVersionO = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

        override fun onAvailable(network: Network) {
            Timber.d("onAvailable() called with: network = $network")
            if (!buildVersionO) {
                cm.getNetworkCapabilities(network)?.let {
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

        private fun checkInternetAvailable(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            if (networkCapabilities.hasCapability(NET_CAPABILITY_VALIDATED)) {
                validNetworks.add(network)
            } else {
                validNetworks.remove(network)
            }
            checkValidNetworks()
        }


        override fun onLost(network: Network) {
            Timber.d("onLost() called with: network = $network")
            validNetworks.remove(network)
            checkValidNetworks()
        }

        override fun onUnavailable() {
            Timber.d("onUnavailable() called")
            validNetworks.clear()
            checkValidNetworks()
        }
    }

    private fun createNetworkCallback() = object : ConnectivityManager.NetworkCallback() {

        /*
            TODO:   I am not sure but an additional process that can be done to check if internet
                    access is working, is checking if there is a captive portal (a portal to
                    authenticate that can hide internet access)
                    https://stackoverflow.com/questions/34576138/how-to-know-the-connected-wifi-is-walled-gardened-captive-portal
         */

        override fun onAvailable(network: Network) {
            Timber.d("onAvailable() called with: network = $network")
            val networkCapabilities = cm.getNetworkCapabilities(network)
            val hasInternetCapability = networkCapabilities?.hasCapability(NET_CAPABILITY_INTERNET)
            Timber.d("onAvailable: $network, internet = $hasInternetCapability")
            if (hasInternetCapability == true) {
                // check if this network actually has internet
                CoroutineScope(Dispatchers.IO).launch {
                    val hasInternet = isInternetAvailable()
                    if (hasInternet) {
                        withContext(Dispatchers.Main) {
                            Timber.d("onAvailable: adding network. $network")
                            validNetworks.add(network)
                            checkValidNetworks()
                        }
                    }
                }
            }
        }

        private fun isInternetAvailable(): Boolean {
            return try {
                val socket = Socket()
                socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
                socket.close()
                true
            } catch (e: IOException) {
                Timber.d("No internet connection. $e")
                false
            }
        }

        override fun onLost(network: Network) {
            Timber.d("onLost() called with: network = $network")
            validNetworks.remove(network)
            checkValidNetworks()
        }

        override fun onUnavailable() {
            //  This method is not being called on Samsumg mobiles.
            Timber.d("onUnavailable() called")
            validNetworks.clear()
            checkValidNetworks()
        }
    }

}


/*
    //  Additional option to NET_CAPABILITY_VALIDATED and socket in order to check
    //  if internet is actually available: Pings...
    //  BUT it has serious compatibility issues and does not work in all devices.
    private fun isInternetAvailable(): Boolean {
        return try {
            val runtime = Runtime.getRuntime()
            val process = runtime.exec("ping -c 1 8.8.8.8")
            val waitFor = process.waitFor()
            waitFor == 0
        } catch (e1: IOException) {
            Timber.d("No internet connection. $e1")
            false
        } catch (e2: InterruptedException) {
            Timber.d("No internet connection. $e2")
            false
        }
    }

 */