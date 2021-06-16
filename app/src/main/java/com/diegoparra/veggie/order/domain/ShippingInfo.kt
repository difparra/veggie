package com.diegoparra.veggie.order.domain

import com.diegoparra.veggie.user.address.domain.Address

data class ShippingInfo(
    val userId: String,
    val address: Address,
    val deliveryDateAndTimeMillis: Long,    //  Date and time. E.g.: Today, 7:00 a.m.
    val timeIntervalSizeMillis: Long,       //  1 hour
    val deliveryCost: Int,
    val state: String           //  Preparing, on the way, delivered
)