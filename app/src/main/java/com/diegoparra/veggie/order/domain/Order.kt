package com.diegoparra.veggie.order.domain

data class Order(
    val id: String,
    val shippingInfo: ShippingInfo,
    val products: ProductsList,
    val total: Total,
    val paymentInfo: PaymentInfo,
    val additionalNotes: String
)