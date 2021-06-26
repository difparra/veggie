package com.diegoparra.veggie.products.data.firebase

import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue
import com.google.firebase.remoteconfig.ktx.get
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class ProductsApi @Inject constructor(
    private val database: FirebaseFirestore,
    private val remoteConfig: FirebaseRemoteConfig,
    private val gson: Gson
) {

    //  Same function as in OrderConfigApi
    private suspend fun <T> getValueFromRemoteConfig(
        configKey: String,
        parseConfigValue: (FirebaseRemoteConfigValue) -> T
    ): Either<Failure, T> {
        return try {
            remoteConfig.fetchAndActivate().await()
            Timber.d("key = $configKey, lastFetchTime = ${remoteConfig.info.fetchTimeMillis}")
            val configValue = remoteConfig[configKey]
            val parsedConfigValue = parseConfigValue(configValue)
            Timber.d("returned value: $parsedConfigValue")
            Either.Right(parsedConfigValue)
        } catch (e: Exception) {
            Timber.e("Exception occur while getting remoteConfig value: $configKey, exceptionClass=${e.javaClass}, exceptionMessage=${e.message}")
            Either.Left(Failure.ServerError(e))
        }
    }

    suspend fun getTags(): Either<Failure, List<TagDto>> = getValueFromRemoteConfig(
        configKey = ProdsFirebaseConstants.RemoteConfigKeys.categories,
        parseConfigValue = { gson.fromJson(it.asString(), TagDtoList::class.java).tagsArray }
    )



    suspend fun getProductsUpdatedAfter(timestamp: Timestamp): Either<Failure, List<ProductDto>> {
        //  It is not really important to get products sorted in here, as long as local database
        //  run its update in a transaction, so that if update was not completed, revert the products
        //  that were updated at that time, having a consistent lastUpdateTime in local database.
        return try {
            val prods = database
                .collection(ProdsFirebaseConstants.Collections.products)
                .whereGreaterThan(ProdsFirebaseConstants.Keys.updatedAt, timestamp)
                .get()
                .await()
                .toObjects<ProductDto>()
            Either.Right(prods)
        } catch (e: Exception) {
            Either.Left(Failure.ServerError(exception = e))
        }
    }

}