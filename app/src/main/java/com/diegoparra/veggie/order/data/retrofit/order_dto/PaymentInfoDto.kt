package com.diegoparra.veggie.order.data.retrofit.order_dto

data class PaymentInfoDto(
    val status: String,
    val paymentMethod: String,
    val details: String,                    //  Information shown to the user.
    //  All information collected in the transaction.
    //  May include info like cardNumber, paidAt, bank, buyerInfo, ticketImage, ...
    val additionalInfo: Map<String, String>?,   //  empty map is considered as null or no value by Gson when serializing, so if deserializing map will be null
    val updatedAt: Long
)