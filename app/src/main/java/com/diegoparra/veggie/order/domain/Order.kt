package com.diegoparra.veggie.order.domain

data class Order(
    val id: String,
    val shippingInfo: ShippingInfo,
    val products: ProductsList,
    val total: Total,
    val paymentInfo: PaymentInfo,
    val additionalNotes: String? = null,
    val state: Status,
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