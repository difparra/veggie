package com.diegoparra.veggie.order.data.order_dto

import com.google.firebase.Timestamp

data class OrderDto(
    val shippingInfo: ShippingInfoDto,
    val products: List<ProductOrderDto>,
    val total: TotalDto,
    val paymentInfo: PaymentInfoDto,
    val additionalNotes: String? = null,
    val status: String,
    val updatedAt: Timestamp
)