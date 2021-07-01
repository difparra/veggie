package com.diegoparra.veggie.core.kotlin

import java.util.concurrent.TimeUnit

/**
 *  With this class repo can easily decide where to fetch data from: Local or Remote.
 *  In addition, there is a new class, that lets the user decide to fetch based on the lastTime
 *  data was fetched from repo.
 */
sealed class Source(val fetchIntervalInMillis: Long) {
    object SERVER : Source(0)
    class RemoteIfExpired(fetchIntervalInMillis: Long) : Source(fetchIntervalInMillis)
    object CACHE : Source(TimeUnit.DAYS.toMillis(30))



    fun isDataExpired(lastUpdatedAt: BasicTime): Boolean {
        return when (this) {
            SERVER -> true
            CACHE -> false
            is RemoteIfExpired ->
                BasicTime.now().millisEpochUTC - lastUpdatedAt.millisEpochUTC > fetchIntervalInMillis
        }
    }
}