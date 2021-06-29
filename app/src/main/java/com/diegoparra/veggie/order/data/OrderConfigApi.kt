package com.diegoparra.veggie.order.data

import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.order.data.DeliveryTimetableDtoTransformations.toDeliveryTimetable
import com.diegoparra.veggie.order.data.OrderFirebaseConstants.RemoteConfigKeys
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue
import com.google.firebase.remoteconfig.ktx.get
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

class OrderConfigApi @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig,
    private val gson: Gson
) {

    /*
        RemoteConfig conclusions:
        fetchTimeMillis:
            ->  Values won't be fetched if minimumFetchInterval has not been reached.
                After calling fetchAndActivate, lastFetchTime didn't changed if minimumFetchInterval was not reached.
            ->  LastFetchTime will survive even when app closes, there is no need to save in shared/datastore prefs.
        If key doesn't exist in remoteConfigServer:
            ->  Will get the default value in xml file, or another default value as 0 if getting double.
            ->  It is not likely it will throw an exception.
        No internet:
            ->  Will be throw exception, such as FirebaseRemoteClientException
     */

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

    suspend fun getMinOrder() = getValueFromRemoteConfig(
        configKey = RemoteConfigKeys.minOrder,
        parseConfigValue = { it.asDouble().toInt() }
    )

    suspend fun getDeliveryBaseCost() = getValueFromRemoteConfig(
        configKey = RemoteConfigKeys.deliveryCostBase,
        parseConfigValue = { it.asDouble().toInt() }
    )

    suspend fun getDeliveryExtraCostSameDay() = getValueFromRemoteConfig(
        configKey = RemoteConfigKeys.deliveryCostExtraSameDay,
        parseConfigValue = { it.asDouble().toInt() }
    )

    suspend fun getDeliveryTimetable() = getValueFromRemoteConfig(
        configKey = RemoteConfigKeys.deliveryTimeTimetable,
        parseConfigValue = {
            gson.fromJson(it.asString(), DeliveryTimetableDto::class.java)
                .toDeliveryTimetable()
        }
    )

    suspend fun getMinTimeForDeliveryInHours() = getValueFromRemoteConfig(
        configKey = RemoteConfigKeys.deliveryTimeMinTimeInHours,
        parseConfigValue = { it.asDouble().toInt() }
    )

    suspend fun getMaxDaysForDelivery() = getValueFromRemoteConfig(
        configKey = RemoteConfigKeys.deliveryTimeMaxDaysAhead,
        parseConfigValue = { it.asDouble().toInt() }
    )

}