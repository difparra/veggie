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
        CREATED,
        PREPARING,
        ON_THE_WAY,
        //  ON_HOLD = WAITING FOR PAYMENT
        DELIVERED,
        CANCELLED,
        REFUNDED
    }

}