package com.diegoparra.veggie.user.address.domain

import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import kotlinx.coroutines.flow.Flow

interface AddressRepository {

    suspend fun getAddressList(userId: String): Either<Failure, List<Address>>
    suspend fun getAddress(userId: String, addressId: String): Either<Failure, Address>
    fun getSelectedAddressAsFlow(userId: String): Flow<Either<Failure, Address?>>

    suspend fun addAddress(userId: String, address: Address): Either<Failure, Unit>
    suspend fun deleteAddress(userId: String, address: Address): Either<Failure, Unit>

    suspend fun setSelectedAddressId(userId: String, addressId: String)
    suspend fun getSelectedAddressId(userId: String): Either<Failure, String>

}

/*
    TODO:   I could think of getting flows from remote database, because they could certainly make
            the code cleaner, shorter and possibly less prone to errors, but they could also call
            the database more times compared to updating just when  getting successful results from
            nav_graph flows.
 */