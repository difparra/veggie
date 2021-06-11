package com.diegoparra.veggie.user.data

data class AddressDto(
    val address: String,
    val details: String?
) {
    //  Required empty constructor for firebase
    constructor() : this(
        address = "",
        details = null
    )
}