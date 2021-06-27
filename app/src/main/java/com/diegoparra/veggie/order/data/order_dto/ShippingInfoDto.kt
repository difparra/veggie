package com.diegoparra.veggie.order.data.order_dto

import com.diegoparra.veggie.user.data.firebase.AddressDto
import com.google.firebase.Timestamp

data class ShippingInfoDto(
    val userId: String,
    val phoneNumber: String,
    val address: AddressDto,
    val deliverySchedule: DeliveryScheduleDto,
    val deliveryCost: Int
)

data class DeliveryScheduleDto(
    val from: Timestamp,
    val to: Timestamp
)