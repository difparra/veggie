package com.diegoparra.veggie.order.data

object OrderFirebaseConstants {

    object RemoteConfigKeys {
        const val minOrder = "min_order"
        const val deliveryCostBase = "delivery_cost_base"
        const val deliveryCostExtraSameDay = "delivery_cost_extra_same_day"
        const val deliveryTimeTimetable = "delivery_time_timetable"
        const val deliveryTimeMinTimeInHours = "delivery_time_min_time_in_hours"
        const val deliveryTimeMaxDaysAhead = "delivery_time_max_days_ahead"
    }

    object Firestore {
        object Collections {
            const val orders = "orders"
        }
        object Fields {
            private const val shippingInfo = "shippingInfo"
            private const val userId = "userId"
            const val userIdComplete = "$shippingInfo.$userId"
            private const val deliverySchedule = "deliverySchedule"
            private const val from = "from"
            const val deliveryDateFrom = "$shippingInfo.$deliverySchedule.$from"
        }
    }

}