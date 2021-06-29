package com.diegoparra.veggie.order.domain

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

data class DeliverySchedule(
    val date: LocalDate,
    val timeRange: TimeRange
)

class TimeRange(
    val from: LocalTime,
    val to: LocalTime
)



//  Config - data that will not be shown directly to the user but will be needed in the use cases
//  to perform some kind of calculation

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