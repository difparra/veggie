package com.diegoparra.veggie.core.kotlin

/**
 *  With this class repo can easily decide where to fetch data from: Local or Remote.
 *  In addition, there is a new class, that lets the user decide to fetch based on the lastTime
 *  data was fetched from repo.
 */
sealed class Source {
    object SERVER : Source()
    object CACHE : Source()

    /**
     * The default behaviour is described as:
     * - If fetchInterval has not been met, data will be fetch from cache.
     * - If data has expired (lastFetch > fetchInterval) data will be collected from server.
     * - If there is some network failure, the data that will be shown is from cache.
     */
    class DEFAULT(val fetchIntervalInMillis: Long = 0) : Source()

    fun mustFetchFromServer(
        lastSuccessfulFetch: BasicTime,
        isInternetAvailable: Boolean
    ): Boolean {
        return when (this) {
            SERVER -> true
            CACHE -> false
            is DEFAULT -> {
                return if (!isInternetAvailable) {
                    false
                } else if (fetchIntervalInMillis == 0L) {
                    true
                } else {
                    BasicTime.now().millisEpochUTC - lastSuccessfulFetch.millisEpochUTC > fetchIntervalInMillis
                }
            }
        }
    }
}