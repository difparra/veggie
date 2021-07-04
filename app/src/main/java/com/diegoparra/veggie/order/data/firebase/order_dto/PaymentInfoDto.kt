package com.diegoparra.veggie.order.data.firebase.order_dto

import com.google.firebase.Timestamp

data class PaymentInfoDto(
    val status: String,
    val paymentMethod: String,
    val details: String,                    //  Information shown to the user.
    //  All information collected in the transaction.
    //  May include info like cardNumber, paidAt, bank, buyerInfo, ticketImage, ...
    val additionalInfo: Map<String, String>,
    val updatedAt: Timestamp
) {
    //  Required empty constructor for firebase
    constructor(): this(
        status = "",
        paymentMethod = "",
        details = "",
        additionalInfo = mapOf(),
        updatedAt = Timestamp(0,0)
    )
}