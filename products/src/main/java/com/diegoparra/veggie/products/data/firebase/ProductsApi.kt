package com.diegoparra.veggie.products.data.firebase

import com.diegoparra.veggie.core.android.LocalUpdateHelper
import com.diegoparra.veggie.core.kotlin.BasicTime
import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.products.data.toTimestamp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue
import com.google.firebase.remoteconfig.ktx.get
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class ProductsApi @Inject constructor(
    private val database: FirebaseFirestore,
    private val remoteConfig: FirebaseRemoteConfig,
    private val gson: Gson
): LocalUpdateHelper.ServerApi<ProductDto> {

    //  Same function as in OrderConfigApi
    private suspend fun <T> getValueFromRemoteConfig(
        configKey: String,
        parseConfigValue: (FirebaseRemoteConfigValue) -> T
    ): Either<Failure, T> {
        return try {
            //  I have already implemented cache logic in repository and this function
            //  will be called just when I want to get server values.
            //  It is safe to put 0 as minimumFetchIntervalInSeconds here.
            remoteConfig.fetch(0).await()
            remoteConfig.activate().await()
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
        parseConfigValue = {
            val type = object : TypeToken<List<TagDto>>() {}.type
            gson.fromJson<List<TagDto>>(it.asString(), type)
        }
    )



    /*
        It is really important to only fetch from server here, as depending on the result
        productsUpdatedAt (in prefs) will be updated or not.
        If this method get also from cache, it will not return a failure, and productsUpdatedAt
        will be updated but with an incorrect value, the prefs will not keep a consistent
        value.
        Firebase cache can be configured not to work, but that would also mean I would have to
        implement cache also for userData, and losing some advantages as pending tasks to
        update database when app is offline.
     */
    override suspend fun getItemsUpdatedAfter(basicTime: BasicTime, userId: String?): Either<Failure, List<ProductDto>> {
        //  It is not really important to get products sorted in here, as long as local database
        //  run its update in a transaction, so that if update was not completed, revert the products
        //  that were updated at that time, having a consistent lastUpdateTime in local database.
        return try {
            val prods = database
                .collection(ProdsFirebaseConstants.Collections.products)
                .whereGreaterThan(ProdsFirebaseConstants.Keys.updatedAt, basicTime.toTimestamp())
                .get(Source.SERVER)
                .await()
                .toObjects<ProductDto>()
            Timber.d("products: $prods")
            Either.Right(prods)
        } catch (e: Exception) {
            Timber.e("Exception class=${e.javaClass}, message=${e.message}")
            Either.Left(Failure.ServerError(exception = e))
        }
    }

}