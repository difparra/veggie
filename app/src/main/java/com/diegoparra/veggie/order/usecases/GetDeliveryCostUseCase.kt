package com.diegoparra.veggie.order.usecases

import com.diegoparra.veggie.ConfigDefaults
import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.order.domain.DefaultConfigValues
import com.diegoparra.veggie.order.domain.DeliveryBaseCosts
import com.diegoparra.veggie.order.domain.DeliverySchedule
import com.diegoparra.veggie.order.domain.OrderRepository
import com.diegoparra.veggie.user.address.domain.Address
import kotlinx.coroutines.*
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

class GetDeliveryCostUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {

    private var deliveryBaseCosts: DeliveryBaseCosts? = null
    private val defaultDeliveryBaseCosts = DeliveryBaseCosts(
        baseCost = DefaultConfigValues.deliveryCostBase,
        extraCostOnSameDay = DefaultConfigValues.deliveryCostExtraSameDay
    )   //  Used when getting an exception from remoteConfigApi, such as when there is no internet

    private suspend fun getDeliveryBaseCosts(): DeliveryBaseCosts {
        return deliveryBaseCosts
            ?: when (val repoValue = orderRepository.getDeliveryBaseCosts()) {
                is Either.Left -> defaultDeliveryBaseCosts
                is Either.Right -> {
                    deliveryBaseCosts = repoValue.b
                    repoValue.b
                }
            }
    }

    suspend operator fun invoke(deliverySchedule: DeliverySchedule?, address: Address?): Int {
        val baseCost = getBaseCost()
        val dateTimeExtraCost = deliverySchedule?.let { getExtraCostDeliveryDateTime(it) } ?: 0
        val addressExtraCost = address?.let { getExtraCostAddress(it) } ?: 0
        Timber.d("deliveryCost = $baseCost + $dateTimeExtraCost + $addressExtraCost")
        return baseCost + dateTimeExtraCost + addressExtraCost
    }

    private suspend fun getBaseCost(): Int {
        return getDeliveryBaseCosts().baseCost
    }

    private suspend fun getExtraCostDeliveryDateTime(deliverySchedule: DeliverySchedule): Int {
        val currentDate = LocalDate.now()
        return if (currentDate.compareTo(deliverySchedule.date) == 0) {
            getDeliveryBaseCosts().extraCostOnSameDay
        } else {
            0
        }
    }

    private fun getExtraCostAddress(address: Address): Int {
        return 0
    }

}