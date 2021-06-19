package com.diegoparra.veggie.order.usecases

import com.diegoparra.veggie.ConfigDefaults
import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.order.domain.DeliverySchedule
import com.diegoparra.veggie.order.domain.DeliveryScheduleConfig
import com.diegoparra.veggie.order.domain.OrderRepository
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class GetDeliveryScheduleOptionsUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {

    private var deliveryScheduleConfig: DeliveryScheduleConfig? = null
    private val defaultDeliveryScheduleConfig = DeliveryScheduleConfig(
        deliveryTimetable = ConfigDefaults.Order.deliveryTimeTimetable,
        minTimeForDeliveryInHours = ConfigDefaults.Order.deliveryTimeMinTimeInHours,
        maxDaysAhead = ConfigDefaults.Order.deliveryTimeMaxDaysAhead
    )

    /*  The principle of this function is the same as in GetMinOrderUseCase */
    private suspend fun getDeliveryScheduleConfig(): DeliveryScheduleConfig {
        //  Return already loaded config value
        deliveryScheduleConfig?.let { return@getDeliveryScheduleConfig it }

        //  Load from repo if value has not been initialized
        return when (val result = orderRepository.getDeliveryScheduleConfig()) {
            is Either.Left -> defaultDeliveryScheduleConfig
            is Either.Right -> {
                deliveryScheduleConfig = result.b
                result.b
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

        val timeRangeOptionsToday =
            deliveryTimetable
                .filter { it.dayOfWeek == currentDate.dayOfWeek }
                .filter { it.timeRange.to.minusHours(1) >= currentTime.plusHours(minTimeForDeliveryInHours.toLong()) }
        deliveryOptions.addAll(timeRangeOptionsToday.map {
            DeliverySchedule(
                date = currentDate,
                timeRange = it.timeRange
            )
        })

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