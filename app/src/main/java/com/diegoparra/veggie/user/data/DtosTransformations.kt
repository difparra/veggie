package com.diegoparra.veggie.user.data

import com.diegoparra.veggie.user.domain.Address
import com.diegoparra.veggie.user.domain.User

object DtosTransformations {

    fun UserDto.toUser() = User(
        id = id,
        email = email,
        name = name,
        phoneNumber = phoneNumber,
        address = address?.map { it.toAddress() }
    )

    fun AddressDto.toAddress() = Address(
        address = address,
        details = details
    )

}