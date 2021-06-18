package com.diegoparra.veggie.order.ui

import java.time.LocalDate
import java.time.LocalTime
import java.time.Month

object FakeDeliveryDates {


    private val days = listOf(
        LocalDate.of(2021, Month.JUNE, 18),
        LocalDate.of(2021, Month.JUNE, 19),
        LocalDate.of(2021, Month.JUNE, 20)
    )

    private data class TimeRange(val from: LocalTime, val to: LocalTime)

    private val timeRanges = listOf(
        TimeRange(from = LocalTime.of(7, 0), to = LocalTime.of(9, 0)),
        TimeRange(from = LocalTime.of(9, 0), to = LocalTime.of(11, 0)),
        TimeRange(from = LocalTime.of(11, 0), to = LocalTime.of(13, 0)),
        TimeRange(from = LocalTime.of(13, 0), to = LocalTime.of(16, 0)),
        TimeRange(from = LocalTime.of(16, 0), to = LocalTime.of(18, 0)),
        TimeRange(from = LocalTime.of(18, 0), to = LocalTime.of(20, 0))
    )

    val delivery = days.flatMap { date ->
        timeRanges.map {
            Delivery(date, it.from, it.to, 5000)
        }
    }


    data class Delivery(val date: LocalDate, val from: LocalTime, val to: LocalTime, val cost: Int)

    fun List<Delivery>.toSubmitList(): List<ShippingDateTimeAdapter.Item> {
        val list = mutableListOf<ShippingDateTimeAdapter.Item>()
        var currentDay: LocalDate? = null
        this.forEach {
            if (it.date != currentDay) {
                list.add(ShippingDateTimeAdapter.Item.Header(it.date))
                currentDay = it.date
            }
            list.add(
                ShippingDateTimeAdapter.Item.DateTime(
                    date = it.date, from = it.from, to = it.to, cost = it.cost
                )
            )
        }
        return list
    }

}