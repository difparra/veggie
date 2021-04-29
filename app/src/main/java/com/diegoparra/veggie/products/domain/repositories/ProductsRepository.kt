package com.diegoparra.veggie.products.domain.repositories

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.products.domain.entities.Tag
import com.diegoparra.veggie.products.domain.entities.MainProduct
import com.diegoparra.veggie.products.domain.entities.VariationProduct
import java.util.concurrent.TimeUnit

interface ProductsRepository {

    suspend fun getTags(
        forceUpdate: Boolean = false,
        expirationTimeMillis: Long = TimeUnit.DAYS.toMillis(1)
    ): Either<Failure, List<Tag>>

    suspend fun getMainProductsByTagId(
        tagId: String,
        forceUpdate: Boolean = false,
        expirationTimeMillis: Long = TimeUnit.MINUTES.toMillis(10)
    ): Either<Failure, List<MainProduct>>

    suspend fun searchMainProductsByName(
        query: String,
        forceUpdate: Boolean = false,
        expirationTimeMillis: Long = TimeUnit.MINUTES.toMillis(10)
    ): Either<Failure, List<MainProduct>>

    suspend fun getProductVariationsByMainId(
        mainId: String,
        forceUpdate: Boolean = false,
        expirationTimeMillis: Long = TimeUnit.MINUTES.toMillis(15)
    ) : Either<Failure, List<VariationProduct>>


    /*
        ForceUpdate is set to false as default. Otherwise, there would be no point on having a local
        database, as products will be always getting from the network.
        However, use cases can decide whether to force the update or not.
        forceUpdate implementations in methods:
            true -> get from network without further evaluation.
            false ->
                    expirationTime ->   if expired, get from network.
                                        if not expired, get from database.
    */

}