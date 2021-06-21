package com.diegoparra.veggie.user.address.domain

import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import kotlinx.coroutines.flow.Flow

interface AddressRepository {

    suspend fun getAddressList(userId: String): Either<Failure, List<Address>>
    suspend fun addAddress(userId: String, address: Address): Either<Failure, Address>
    suspend fun deleteAddress(userId: String, address: Address): Either<Failure, Unit>

    suspend fun setSelectedAddressId(userId: String, addressId: String)
    suspend fun getSelectedAddressId(userId: String): Either<Failure, String?>

}