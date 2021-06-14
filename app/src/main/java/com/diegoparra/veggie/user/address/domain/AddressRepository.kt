package com.diegoparra.veggie.user.address.domain

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure

interface AddressRepository {

    suspend fun getAddressList(userId: String): Either<Failure, List<Address>>
    suspend fun addAddress(userId: String, address: Address): Either<Failure, Unit>
    suspend fun deleteAddress(userId: String, address: Address): Either<Failure, Unit>
    suspend fun setSelectedAddressId(addressId: String)
    suspend fun getSelectedAddressId(): Either<Failure, String>

}