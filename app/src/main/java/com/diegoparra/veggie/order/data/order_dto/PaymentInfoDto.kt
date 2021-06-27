package com.diegoparra.veggie.order.data.order_dto

import com.diegoparra.veggie.order.domain.PaymentMethod
import com.google.firebase.Timestamp

data class PaymentInfoDto(
    val status: String,
    val paymentMethod: PaymentMethodDto,
    val total: Double,
    val paidAt: Timestamp
)

data class PaymentMethodDto(
    val method: String,
    val additionalInfo: Map<String, String>
)