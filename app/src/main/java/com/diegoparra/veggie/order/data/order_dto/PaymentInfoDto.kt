package com.diegoparra.veggie.order.data.order_dto

import com.google.firebase.Timestamp

data class PaymentInfoDto(
    val status: String,
    val paymentMethod: PaymentMethodDto,
    val total: Double,
    val paidAt: Timestamp?
) {
    //  Required empty constructor for firebase
    constructor(): this(
        status = "",
        paymentMethod = PaymentMethodDto(),
        total = -1.0,
        paidAt = Timestamp(0,0)
    )
}

data class PaymentMethodDto(
    val method: String,
    val additionalInfo: Map<String, String>
) {
    //  Required empty constructor for firebase
    constructor(): this(
        method = "",
        additionalInfo = mapOf()
    )
}