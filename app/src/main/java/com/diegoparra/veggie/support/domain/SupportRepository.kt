package com.diegoparra.veggie.support.domain

import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure

interface SupportRepository {

    suspend fun getContactEmail(): Either<Failure, String>
    suspend fun getContactPhoneNumber(): Either<Failure, String>

}