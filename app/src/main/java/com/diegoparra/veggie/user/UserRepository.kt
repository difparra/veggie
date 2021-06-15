package com.diegoparra.veggie.user

import com.diegoparra.veggie.user.address.domain.Address
import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.user.address.domain.AddressRepository

interface UserRepository : AddressRepository {

    suspend fun updateUserData(
        userId: String,
        email: String? = null,
        name: String? = null,
        phoneNumber: String? = null,
        addressList: List<Address>? = null
    ): Either<Failure, Unit>

    suspend fun getUser(userId: String): Either<Failure, User>

}