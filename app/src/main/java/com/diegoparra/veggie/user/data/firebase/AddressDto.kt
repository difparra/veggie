package com.diegoparra.veggie.user.data.firebase

data class AddressDto(
    val id: String,
    val address: String,
    val details: String?,
    val instructions: String?
) {
    //  Required empty constructor for firebase
    constructor() : this(
        id = "",
        address = "",
        details = null,
        instructions = null
    )
}