package com.diegoparra.veggie.order.ui.ui_utils

import android.content.Context
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.formatApp
import com.diegoparra.veggie.order.domain.DeliverySchedule
import com.diegoparra.veggie.order.domain.Order

fun DeliverySchedule.printFullWithShortFormat(): String {
    return this.date.formatApp(short = true) + "; " +
            Pair(this.timeRange.from, this.timeRange.to).formatApp()
}

fun Order.Status.print(context: Context): String {
    val resource = when(this) {
        Order.Status.STARTED -> R.string.order_status_started
        Order.Status.CREATED -> R.string.order_status_created
        Order.Status.PREPARING -> R.string.order_status_preparing
        Order.Status.ON_THE_WAY -> R.string.order_status_on_the_way
        Order.Status.DELIVERED -> R.string.order_status_delivered
        Order.Status.CANCELLED -> R.string.order_status_cancelled
        Order.Status.REFUNDED -> R.string.order_status_refunded
    }
    return context.getString(resource)
}