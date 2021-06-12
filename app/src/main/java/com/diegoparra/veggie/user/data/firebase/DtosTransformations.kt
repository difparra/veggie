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
        details = details
    )

    fun Address.toAddressDto() = AddressDto(
        id = UUID.randomUUID().toString(),
        address = address,
        details = if(details.isNullOrBlank()) null else details
    )

}