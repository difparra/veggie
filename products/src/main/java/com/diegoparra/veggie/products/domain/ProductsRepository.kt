package com.diegoparra.veggie.products.domain

import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.Source
import java.util.concurrent.TimeUnit

interface ProductsRepository {

    suspend fun getTags(
        source: Source = Source.RemoteIfExpired(TimeUnit.DAYS.toMillis(1))
    ): Either<Failure, List<Tag>>

    suspend fun getMainProductsByTagId(
        tagId: String,
        source: Source = Source.RemoteIfExpired(DEFAULT_FETCH_INTERVAL_MILLIS)
    ): Either<Failure, List<Product>>

    suspend fun searchMainProductsByName(
        query: String,
        source: Source = Source.RemoteIfExpired(DEFAULT_FETCH_INTERVAL_MILLIS)
    ): Either<Failure, List<Product>>

    suspend fun getVariationsByMainId(
        mainId: String,
        source: Source = Source.RemoteIfExpired(DEFAULT_FETCH_INTERVAL_MILLIS)
    ): Either<Failure, List<VariationData>>

    suspend fun getProduct(
        mainId: String, varId: String,
        source: Source = Source.RemoteIfExpired(DEFAULT_FETCH_INTERVAL_MILLIS)
    ): Either<Failure, Product>


    companion object {
        const val DEFAULT_FETCH_INTERVAL_MILLIS: Long = 10 * 60 * 1000      //  10 minutes

        /**
         * Will return CACHE if internet is not available.
         * Otherwise it will try to return RemoteIfExpired with a defaultFetchInterval of (-10 min-)
         */
        fun getDefaultSourceForInternetAccessState(
            isInternetAvailable: Boolean
        ): Source {
            return if (!isInternetAvailable) {
                return Source.CACHE
            } else {
                if (DEFAULT_FETCH_INTERVAL_MILLIS > 0) {
                    Source.RemoteIfExpired(DEFAULT_FETCH_INTERVAL_MILLIS)
                } else {
                    Source.SERVER
                }
            }
        }
    }
}