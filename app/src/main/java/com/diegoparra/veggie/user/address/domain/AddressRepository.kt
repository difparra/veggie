package com.diegoparra.veggie.user.address.domain

import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import kotlinx.coroutines.flow.Flow

interface AddressRepository {

    suspend fun getAddressList(userId: String): Either<Failure, List<Address>>
    suspend fun addAddress(userId: String, address: Address): Either<Failure, Address>
    suspend fun deleteAddress(userId: String, address: Address): Either<Failure, Unit>

    suspend fun setSelectedAddress(userId: String, addressId: String)
    suspend fun getSelectedAddress(userId: String, addressList: List<Address>? = null): Either<Failure, Address?>

}