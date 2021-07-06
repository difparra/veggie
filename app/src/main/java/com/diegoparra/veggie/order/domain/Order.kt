package com.diegoparra.veggie.order.domain

import com.diegoparra.veggie.core.kotlin.BasicTime

data class Order(
    val id: String,
    val shippingInfo: ShippingInfo,
    val products: ProductsList,
    val total: Total,
    val paymentInfo: PaymentInfo,
    val additionalNotes: String? = null,
    val status: Status,
    val updatedAt: BasicTime
) {
    enum class Status {
        //  When payment is not CASH, it will be created as started, and when payment
        //  has been confirmed it could be changed to created.
        STARTED,
        CREATED,
        PREPARING,
        ON_THE_WAY,
        DELIVERED,
        CANCELLED,
        REFUNDED
    }
}
