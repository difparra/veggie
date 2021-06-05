package com.diegoparra.veggie.user.domain

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure

interface UserRepository {

    suspend fun getPhoneNumber(): Either<Failure, String>
    suspend fun getAddress(): Either<Failure, String>


}