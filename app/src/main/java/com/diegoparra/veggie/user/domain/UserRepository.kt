package com.diegoparra.veggie.user.domain

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure

interface UserRepository {

    suspend fun updateUserData(
        id: String,
        email: String? = null,
        name: String? = null,
        phoneNumber: String? = null,
        address: String? = null
    ): Either<Failure, Unit>

    suspend fun getUser(id: String) : Either<Failure, User>

    suspend fun getAddressListForUser(id: String): Either<Failure, List<Address>>

}