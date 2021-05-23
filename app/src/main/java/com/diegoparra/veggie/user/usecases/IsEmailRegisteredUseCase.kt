package com.diegoparra.veggie.user.usecases

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.validateEmail
import com.diegoparra.veggie.user.entities_and_repo.UserRepository
import javax.inject.Inject

class IsEmailRegisteredUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(email: String) : Either<Failure, Boolean> {
        //  TODO: Check email state in firestore
        return Either.Right(true)
    }

}