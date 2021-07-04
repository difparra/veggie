package com.diegoparra.veggie.order.data.firebase.order_dto

import com.google.firebase.Timestamp

data class OrderDto(
    val shippingInfo: ShippingInfoDto,
    val products: List<ProductOrderDto>,
    val total: TotalDto,
    val paymentInfo: PaymentInfoDto,
    val additionalNotes: String? = null,
    val status: String,
    val updatedAt: Timestamp
) {
    //  Required empty constructor for firebase
    constructor(): this(
        shippingInfo = ShippingInfoDto(),
        products = listOf(),
        total = TotalDto(),
        paymentInfo = PaymentInfoDto(),
        additionalNotes = null,
        status = "",
        updatedAt = Timestamp(0,0)
    )
}