package com.diegoparra.veggie.user.data.firebase

import com.diegoparra.veggie.address.Address
import com.diegoparra.veggie.user.domain.User
import java.util.*

object DtosTransformations {

    fun UserDto.toUser() = User(
        id = id,
        email = email,
        name = name,
        phoneNumber = phoneNumber,
        address = addressList?.map { it.toAddress() }
    )

    fun AddressDto.toAddress() = Address(
        id = id,
        address = address,
        details = details,
        instructions = instructions
    )

    fun Address.toAddressDto() = AddressDto(
        id = id,
        address = address,
        details = if(details.isNullOrBlank()) null else details,
        instructions = if(instructions.isNullOrBlank()) null else instructions
    )

}