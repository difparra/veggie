package com.diegoparra.veggie.order.domain

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class TimeRange(
    val from: LocalTime,
    val to: LocalTime
)

data class DeliverySchedule(
    val date: LocalDate,
    val timeRange: TimeRange
)


data class DeliveryBaseCosts(
    val baseCost: Int,
    val extraCostOnSameDay: Int
)


data class DeliveryScheduleConfig(
    val deliveryTimetable: List<TimetableItem>,
    val minTimeForDeliveryInHours: Int,
    val maxDaysAhead: Int
) {
    data class TimetableItem(
        val dayOfWeek: DayOfWeek,
        val timeRange: TimeRange
    )
}