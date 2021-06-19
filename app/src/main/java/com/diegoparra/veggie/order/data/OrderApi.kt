package com.diegoparra.veggie.order.data

import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.order.data.DeliveryTimetableDtoTransformations.toDeliveryTimetable
import com.diegoparra.veggie.order.domain.DeliveryBaseCosts
import com.diegoparra.veggie.order.domain.DeliveryScheduleConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.get
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class OrderApi @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig,
    private val gson: Gson
) {

    suspend fun getMinOrder(): Either<Failure, Int> {
        return try {
            remoteConfig.fetchAndActivate().await()
            val minOrder = remoteConfig[OrderFirebaseConstants.RemoteConfigKeys.minOrder].asDouble()
            Timber.d("minOrder = $minOrder")
            Either.Right(minOrder.toInt())
        } catch (e: Exception) {
            Timber.e("Error getting minOrder: $e")
            Either.Left(Failure.ServerError(exception = e))
        }
    }

    suspend fun getDeliveryBaseCosts(): Either<Failure, DeliveryBaseCosts> {
        return try {
            remoteConfig.fetchAndActivate().await()
            val baseCost =
                remoteConfig[OrderFirebaseConstants.RemoteConfigKeys.deliveryCostBase].asDouble()
            Timber.d("baseCost = $baseCost")
            val extraCostSameDay =
                remoteConfig[OrderFirebaseConstants.RemoteConfigKeys.deliveryCostExtraSameDay].asDouble()
            Timber.d("extraCostSameDay = $extraCostSameDay")

            Either.Right(
                DeliveryBaseCosts(
                    baseCost = baseCost.toInt(),
                    extraCostOnSameDay = extraCostSameDay.toInt()
                )
            )
        } catch (e: Exception) {
            Timber.e("Error getting deliveryBaseCosts: $e")
            Either.Left(Failure.ServerError(exception = e))
        }
    }

    suspend fun getDeliveryScheduleConfig(): Either<Failure, DeliveryScheduleConfig> {
        return try {
            remoteConfig.fetch(TimeUnit.HOURS.toSeconds(1)).await()
            remoteConfig.activate().await()

            val deliveryTimetable =
                getDeliveryTimetable().toDeliveryTimetable()
            Timber.d("deliveryTimetable = ${deliveryTimetable.map { 
                "dayOfWeek = ${it.dayOfWeek}, from = ${it.timeRange.from}, to = ${it.timeRange.to}"
            }.joinToString("\n")}")
            val minTimeForDeliveryInHours =
                remoteConfig[OrderFirebaseConstants.RemoteConfigKeys.deliveryTimeMinTimeInHours].asDouble()
            Timber.d("minTimeForDeliveryInHours = $minTimeForDeliveryInHours")
            val maxDaysAhead =
                remoteConfig[OrderFirebaseConstants.RemoteConfigKeys.deliveryTimeMaxDaysAhead].asDouble()
            Timber.d("maxDaysAhead = $maxDaysAhead")

            Either.Right(
                DeliveryScheduleConfig(
                    deliveryTimetable = deliveryTimetable,
                    minTimeForDeliveryInHours = minTimeForDeliveryInHours.toInt(),
                    maxDaysAhead = maxDaysAhead.toInt()
                )
            )
        } catch (e: Exception) {
            Timber.e("Error getting deliveryBaseCosts: $e")
            Either.Left(Failure.ServerError(exception = e))
        }
    }

    private fun getDeliveryTimetable(): DeliveryTimetableDto {
        val deliveryTimetableString =
            remoteConfig[OrderFirebaseConstants.RemoteConfigKeys.deliveryTimeTimetable].asString()
        Timber.d("deliveryTimetableString = $deliveryTimetableString")
        return gson.fromJson(deliveryTimetableString, DeliveryTimetableDto::class.java)
    }


}