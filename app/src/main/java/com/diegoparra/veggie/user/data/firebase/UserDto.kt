package com.diegoparra.veggie.user.data.firebase

import com.google.firebase.firestore.DocumentId

data class UserDto(
    @DocumentId val id: String,
    val email: String,
    val name: String,
    val phoneNumber: String?,
    val addressList: List<AddressDto>?
) {
    //  Required empty constructor for firebase
    constructor() : this(
        id = "",
        email = "",
        name = "",
        phoneNumber = null,
        addressList = null
    )
}