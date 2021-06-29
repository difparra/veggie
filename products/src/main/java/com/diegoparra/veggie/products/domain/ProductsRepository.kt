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
        source: Source = Source.RemoteIfExpired(TimeUnit.MINUTES.toMillis(10))
    ): Either<Failure, List<Product>>

    suspend fun searchMainProductsByName(
        query: String,
        source: Source = Source.RemoteIfExpired(TimeUnit.MINUTES.toMillis(10))
    ): Either<Failure, List<Product>>

    suspend fun getVariationsByMainId(
        mainId: String,
        source: Source = Source.RemoteIfExpired(TimeUnit.MINUTES.toMillis(15))
    ): Either<Failure, List<VariationData>>

    suspend fun getProduct(
        mainId: String, varId: String,
        source: Source = Source.RemoteIfExpired(TimeUnit.MINUTES.toMillis(10))
    ): Either<Failure, Product>

}