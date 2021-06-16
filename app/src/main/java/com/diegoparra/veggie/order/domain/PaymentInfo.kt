package com.diegoparra.veggie.order.domain

data class PaymentInfo(
    val paymentId: String,
    val paymentType: String,    //  Bank(...), cash
    val date: Long,
    val total: Int,
    val state: String             //  Verifying, verified, paid
)