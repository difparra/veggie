package com.diegoparra.veggie.user.data

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.user.domain.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor() : UserRepository {

    override suspend fun getPhoneNumber(): Either<Failure, String> {
        //  TODO: Get phoneNumber from firestore
        return Either.Right("301 234 5678")
    }

    override suspend fun getAddress(): Either<Failure, String> {
        //  TODO: Get address from firestore
        return Either.Right("Calle ___ # __ - __")
    }


}