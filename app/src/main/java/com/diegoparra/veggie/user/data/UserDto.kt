package com.diegoparra.veggie.user.data

data class UserDto (
    val id: String,
    val email: String,
    val name: String,
    val phoneNumber: String?,
    val address: String?
) {
    //  Required empty constructor for firebase
    constructor() : this(
        id = "",
        email = "",
        name = "",
        phoneNumber = null,
        address = null
    )

}