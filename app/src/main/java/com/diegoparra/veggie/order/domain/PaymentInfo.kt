package com.diegoparra.veggie.order.domain

data class PaymentInfo(
    val status: PaymentStatus,
    val paymentMethod: PaymentMethod,      //  Bank, cardNumber, ...
    val total: Double,              //  Total effectively paid
    val paidAt: Long
)

enum class PaymentStatus {
    PENDING,
    CONFIRMED
}

sealed class PaymentMethod {
    class Cash(val payWith: String?): PaymentMethod()
    class Pos(val ticket: String): PaymentMethod()      //  Point of sale / datáfono
    class Card: PaymentMethod()
}


/*
//  Could be included in car if necessary
data class BillingUser(
    val name: String,
    val phoneNumber: String,
    val address: String
)*/
