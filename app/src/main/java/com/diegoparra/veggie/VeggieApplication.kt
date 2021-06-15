package com.diegoparra.veggie

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltAndroidApp
class VeggieApplication : Application() {

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        asyncInitTimber()
    }

    private fun asyncInitTimber() {
        applicationScope.launch {
            if(BuildConfig.DEBUG) {
                Timber.plant(Timber.DebugTree())
            }
        }
    }

}