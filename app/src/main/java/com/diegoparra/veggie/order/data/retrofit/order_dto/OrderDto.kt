package com.diegoparra.veggie.order.data.retrofit.order_dto

import com.diegoparra.veggie.core.Exclude

data class OrderDto(
    @Exclude val id: String,
    val shippingInfo: ShippingInfoDto,
    val products: List<ProductOrderDto>,
    val total: TotalDto,
    val paymentInfo: PaymentInfoDto,
    val additionalNotes: String? = null,
    val status: String,
    val updatedAt: Long
)