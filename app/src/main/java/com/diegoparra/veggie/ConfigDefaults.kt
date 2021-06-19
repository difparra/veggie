package com.diegoparra.veggie

import com.diegoparra.veggie.order.domain.DeliveryScheduleConfig.TimetableItem
import com.diegoparra.veggie.order.domain.TimeRange
import java.time.DayOfWeek
import java.time.LocalTime

object ConfigDefaults {

    object Order {
        const val minOrder = 50000
        const val deliveryCostBase = 3000
        const val deliveryCostExtraSameDay = 2000

        private val daysOfWeek = listOf(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY,
            DayOfWeek.SUNDAY
        )
        private val timeRanges = listOf(
            TimeRange(from = LocalTime.of(9, 0), to = LocalTime.of(13, 0)),
            TimeRange(from = LocalTime.of(14, 0), to = LocalTime.of(18, 0))
        )
        val deliveryTimeTimetable = daysOfWeek.flatMap { dayOfWeek ->
            timeRanges.map { TimetableItem(dayOfWeek = dayOfWeek, timeRange = it) }
        }
        const val deliveryTimeMinTimeInHours = 5
        const val deliveryTimeMaxDaysAhead = 3


    }

}