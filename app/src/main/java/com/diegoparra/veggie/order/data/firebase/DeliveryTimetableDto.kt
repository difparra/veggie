package com.diegoparra.veggie.order.data.firebase

import android.annotation.SuppressLint
import com.diegoparra.veggie.order.domain.DeliveryScheduleConfig
import com.diegoparra.veggie.order.domain.TimeRange
import com.google.gson.annotations.SerializedName
import timber.log.Timber
import java.time.DayOfWeek
import java.time.LocalTime

data class DeliveryTimetableDto(
    @SerializedName("monday") val monday: List<String>,
    @SerializedName("tuesday") val tuesday: List<String>,
    @SerializedName("wednesday") val wednesday: List<String>,
    @SerializedName("thursday") val thursday: List<String>,
    @SerializedName("friday") val friday: List<String>,
    @SerializedName("saturday") val saturday: List<String>,
    @SerializedName("sunday") val sunday: List<String>,
)



object DeliveryTimetableDtoTransformations {

    @SuppressLint("BinaryOperationInTimber")
    fun DeliveryTimetableDto.toDeliveryTimetable(): List<DeliveryScheduleConfig.TimetableItem> {
        Timber.d("deliveryTimetableDto = " +
                "monday = ${monday.joinToString(", ")}, \n " +
                "tuesday = ${tuesday.joinToString(", ")}, \n " +
                "wednesday = ${wednesday.joinToString(", ")}, \n " +
                "thursday = ${thursday.joinToString(", ")}, \n " +
                "friday = ${friday.joinToString(", ")}, \n " +
                "saturday = ${saturday.joinToString(", ")}, \n " +
                "sunday = ${sunday.joinToString(", ")}")
        val options = mutableListOf<DeliveryScheduleConfig.TimetableItem>()
        options.addAll(toTimeRangesForDay(dayOfWeek = DayOfWeek.MONDAY, timeRanges = monday))
        options.addAll(toTimeRangesForDay(dayOfWeek = DayOfWeek.TUESDAY, timeRanges = tuesday))
        options.addAll(toTimeRangesForDay(dayOfWeek = DayOfWeek.WEDNESDAY, timeRanges = wednesday))
        options.addAll(toTimeRangesForDay(dayOfWeek = DayOfWeek.THURSDAY, timeRanges = thursday))
        options.addAll(toTimeRangesForDay(dayOfWeek = DayOfWeek.FRIDAY, timeRanges = friday))
        options.addAll(toTimeRangesForDay(dayOfWeek = DayOfWeek.SATURDAY, timeRanges = saturday))
        options.addAll(toTimeRangesForDay(dayOfWeek = DayOfWeek.SUNDAY, timeRanges = sunday))
        return options
    }

    private fun toTimeRangesForDay(
        dayOfWeek: DayOfWeek,
        timeRanges: List<String>
    ): List<DeliveryScheduleConfig.TimetableItem> {
        return timeRanges.map {
            DeliveryScheduleConfig.TimetableItem(
                dayOfWeek = dayOfWeek,
                timeRange = toTimeRange(str = it)
            )
        }
    }

    private fun toTimeRange(str: String): TimeRange {
        val hours = str.split("-").map { it.trim().toInt() }
        return TimeRange(from = LocalTime.of(hours[0], 0), to = LocalTime.of(hours[1], 0))
    }

}