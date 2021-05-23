package com.diegoparra.veggie.user.usecases

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.user.entities_and_repo.IsEmailRegistered
import com.diegoparra.veggie.user.entities_and_repo.UserRepository
import javax.inject.Inject

class ValidateEmailUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(email: String) : Either<Failure, IsEmailRegistered> {
        //  Check correct format for email
        validateEmail(email)?.let {
            return Either.Left(it)
        }
        //  Operation in repository/firestore
        return checkIsEmailRegistered(email)
    }

    private fun validateEmail(email: String) : Failure? {
        return if(email.isEmpty()){
            Failure.SignInFailure.EmptyField
        }else if(!com.diegoparra.veggie.core.validateAndSetEmail(email)){
            Failure.SignInFailure.InvalidEmail
        }else{
            null
        }
    }

    private suspend fun checkIsEmailRegistered(email: String) : Either<Failure, IsEmailRegistered> {
        //  TODO: Check email state in firestore
        return Either.Right(IsEmailRegistered(true))
    }

}