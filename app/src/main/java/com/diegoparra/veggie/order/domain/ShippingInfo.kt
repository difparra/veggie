package com.diegoparra.veggie.order.domain

import com.diegoparra.veggie.user.address.domain.Address

data class ShippingInfo(
    val userId: String,
    val phoneNumber: String,
    val address: Address,
    val deliverySchedule: DeliverySchedule,
    val deliveryCost: Int,
    val state: String                       //  Preparing, on the way, delivered
)