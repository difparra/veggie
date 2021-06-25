package com.diegoparra.veggie.order.domain

import java.time.LocalDateTime

data class PaymentInfo(
    val status: PaymentStatus,
    val paymentMethod: String,      //  Bank, cardNumber, ...
    val total: Double,              //  Total effectively paid
    val paidAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

enum class PaymentStatus {
    PENDING,
    CONFIRMED
}

sealed class PaymentMethod {
    class Cash(val payWith: String?): PaymentMethod()
    class Pos(val ticket: String): PaymentMethod()      //  Point of sale / datáfono
    class Card()
}


/*
//  Could be included in car if necessary
data class BillingUser(
    val name: String,
    val phoneNumber: String,
    val address: String
)*/
