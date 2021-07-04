package com.diegoparra.veggie.order.data.room.entities

data class AddressEntity(
    val id: String,
    val address: String,
    val details: String?,
    val instructions: String?
)