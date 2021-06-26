package com.diegoparra.veggie.core.kotlin

import java.util.concurrent.TimeUnit

/**
 *  With this class repo can easily decide where to fetch data from: Local or Remote.
 *  In addition, there is a new class, that lets the user decide to fetch based on the lastTime
 *  data was fetched from repo.
 */
sealed class Source(val fetchIntervalInMillis: Long) {
    object REMOTE : Source(0)
    class REMOTE_IF_EXPIRED(fetchIntervalInMillis: Long) : Source(fetchIntervalInMillis)
    object CACHE : Source(TimeUnit.DAYS.toMillis(30))



    fun isDataExpired(lastUpdatedAtMillis: Long): Boolean {
        return when (this) {
            REMOTE -> true
            CACHE -> false
            is REMOTE_IF_EXPIRED ->
                System.currentTimeMillis() - lastUpdatedAtMillis > fetchIntervalInMillis
        }
    }
}