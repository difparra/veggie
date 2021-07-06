package com.diegoparra.veggie.core

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
 *  - Online status is checked by:
 *    - On APIs greater than 23:
 *          Using NET_CAPABILITY_VALIDATED, which offers the android SDK and could be the best
 *          option to check if internet access is actually working. Working when I tested on my
 *          device with working wifi, and with mobile data on but not working.
 *    - On APIs between 21 and 23:
 *          Checked Internet access creating socket to google server.
 *          Checked there is no captive portal with url: "http://clients3.google.com/generate_204"
 *    ** Note / Disclaimer:
 *    - I have tried the best to get working methods that can check internet access. And even when I
 *      get this working in my device using both methods (APIs greater than 23 and APIs greater than 21),
 *      there are still things I don't know about, for example internet proxies and so on, and,
 *      on the other hand, it is known there can be compatibility issues with some methods.
 *      However, I think this is a great approach to check if internet is actually working, and has
 *      the more important features: checking internet access connecting/pinging to a server (google),
 *      and checking there is no captive portal.
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
            registerNetworkCallbackForApi23()
        } else {
            //  Beware of adding android:usesCleartextTraffic="true" to manifest in case of being neccesary
            registerNetworkCallbackForApi21()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun registerNetworkCallbackForApi23() {
        networkCallback = NetworkCallbackApi23(
            connectivityManager = cm,
            onNetworkVerified = { validNetworks.add(it); checkValidNetworks() },
            onNetworkLost = { validNetworks.remove(it); checkValidNetworks() },
            onNetworksUnavailable = { validNetworks.clear(); checkValidNetworks() }
        )
        val networkRequest = NetworkCallbackApi23.getNetworkRequest()
        cm.registerNetworkCallback(networkRequest, networkCallback)
    }

    private fun registerNetworkCallbackForApi21() {
        networkCallback = NetworkCallbackApi21(
            connectivityManager = cm,
            onNetworkVerified = { validNetworks.add(it); checkValidNetworks() },
            onNetworkLost = { validNetworks.remove(it); checkValidNetworks() },
            onNetworksUnavailable = { validNetworks.clear(); checkValidNetworks() }
        )
        val networkRequest = NetworkCallbackApi21.getNetworkRequest()
        cm.registerNetworkCallback(networkRequest, networkCallback)
    }


    override fun onInactive() {
        Timber.d("onInactive() called")
        cm.unregisterNetworkCallback(networkCallback)
    }

}