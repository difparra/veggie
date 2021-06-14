package com.diegoparra.veggie.user.address.domain

data class Address(
    val id: String,
    val address: String,
    val details: String?,
    val instructions: String?
)