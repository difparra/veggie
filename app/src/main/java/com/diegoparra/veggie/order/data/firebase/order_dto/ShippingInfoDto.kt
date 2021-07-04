package com.diegoparra.veggie.order.data.firebase.order_dto

import com.diegoparra.veggie.user.data.firebase.AddressDto
import com.google.firebase.Timestamp

data class ShippingInfoDto(
    val userId: String,
    val phoneNumber: String,
    val address: AddressDto,
    val deliverySchedule: DeliveryScheduleDto,
    val deliveryCost: Int
) {
    //  Required empty constructor for firebase
    constructor() : this(
        userId = "",
        phoneNumber = "",
        address = AddressDto(),
        deliverySchedule = DeliveryScheduleDto(),
        deliveryCost = -1
    )
}

data class DeliveryScheduleDto(
    val from: Timestamp,
    val to: Timestamp
) {
    //  Required empty constructor for firebase
    constructor(): this(
        from = Timestamp(0,0),
        to = Timestamp(0,0)
    )
}