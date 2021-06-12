package com.diegoparra.veggie.user.domain

import com.diegoparra.veggie.address.Address
import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure

interface UserRepository {

    suspend fun updateUserData(
        userId: String,
        email: String? = null,
        name: String? = null,
        phoneNumber: String? = null,
        addressList: List<Address>? = null
    ): Either<Failure, Unit>

    suspend fun getUser(userId: String) : Either<Failure, User>

    suspend fun getAddressList(userId: String): Either<Failure, List<Address>>
    suspend fun addAddress(userId: String, address: Address): Either<Failure, Unit>
    suspend fun deleteAddress(userId: String, address: Address): Either<Failure, Unit>
    suspend fun setSelectedAddressId(addressId: String)
    suspend fun getSelectedAddressId(): Either<Failure, String>

}