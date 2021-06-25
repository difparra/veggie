package com.diegoparra.veggie.order.usecases

import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.order.domain.DefaultConfigValues
import com.diegoparra.veggie.order.domain.DeliverySchedule
import com.diegoparra.veggie.order.domain.DeliveryScheduleConfig
import com.diegoparra.veggie.order.domain.OrderRepository
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class GetDeliveryScheduleOptionsUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {

    private var deliveryScheduleConfig: DeliveryScheduleConfig? = null
    private val defaultDeliveryScheduleConfig = DeliveryScheduleConfig(
        deliveryTimetable = DefaultConfigValues.deliveryTimeTimetable,
        minTimeForDeliveryInHours = DefaultConfigValues.deliveryTimeMinTimeInHours,
        maxDaysAhead = DefaultConfigValues.deliveryTimeMaxDaysAhead
    )   //  Used when getting an exception from remoteConfigApi, such as when there is no internet

    private suspend fun getDeliveryScheduleConfig(): DeliveryScheduleConfig {
        return deliveryScheduleConfig
            ?: when (val repoValue = orderRepository.getDeliveryScheduleConfig()) {
                is Either.Left -> defaultDeliveryScheduleConfig
                is Either.Right -> {
                    deliveryScheduleConfig = repoValue.b
                    repoValue.b
                }
            }
    }


    /*
        Value that should be fetched more frequently (every hour)
     */

    suspend operator fun invoke(): List<DeliverySchedule> {
        return getDeliveryScheduleOptions(
            deliveryTimetable = getDeliveryScheduleConfig().deliveryTimetable,
            minTimeForDeliveryInHours = getDeliveryScheduleConfig().minTimeForDeliveryInHours,
            maxDaysAhead = getDeliveryScheduleConfig().maxDaysAhead
        )
    }

    private fun getDeliveryScheduleOptions(
        deliveryTimetable: List<DeliveryScheduleConfig.TimetableItem>,
        minTimeForDeliveryInHours: Int,
        maxDaysAhead: Int
    ): List<DeliverySchedule> {
        val currentDate = LocalDate.now()
        val currentTime = LocalTime.now()
        val deliveryOptions = mutableListOf<DeliverySchedule>()

        //  Corrected and verified, be careful when changing/refactoring this method.
        //  Check that time left to finish the day is enough to meet the minTimeForDelivery on the sameDay.
        if((24 - currentTime.hour) > minTimeForDeliveryInHours) {
            val timeRangeOptionsToday =
                deliveryTimetable
                    .filter { it.dayOfWeek == currentDate.dayOfWeek }
                    .filter { it.timeRange.to >= currentTime.plusHours(minTimeForDeliveryInHours.toLong()) }
            deliveryOptions.addAll(timeRangeOptionsToday.map {
                DeliverySchedule(
                    date = currentDate,
                    timeRange = it.timeRange
                )
            })
        }

        if (maxDaysAhead > 0) {
            for (day in 1..maxDaysAhead) {
                val date = currentDate.plusDays(day.toLong())
                val timeRangeOptions = deliveryTimetable.filter { it.dayOfWeek == date.dayOfWeek }
                deliveryOptions.addAll(timeRangeOptions.map {
                    DeliverySchedule(
                        date = date,
                        timeRange = it.timeRange
                    )
                })
            }
        }

        return deliveryOptions
    }


}