package com.diegoparra.veggie.user.usecases

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.SignInFailure
import com.diegoparra.veggie.core.getFailures
import com.diegoparra.veggie.user.entities_and_repo.SignInMethod
import com.diegoparra.veggie.user.entities_and_repo.UserRepository

abstract class EmailAuthUseCase<Params>(
    protected val userRepository: UserRepository
) {

    suspend operator fun invoke(params: Params): Either<Failure, Unit> {
        validateFields(params).let {
            val failures = it.getFailures()
            if(failures.isNotEmpty()){
                return Either.Left(SignInFailure.WrongInputList(failures))
            }
        }
        validateEnabledAuthMethod(params).let {
            if (it is Either.Left) {
                return it
            }
        }
        signInRepository(params).let {
            if (it is Either.Left) {
                return it
            }
        }
        return Either.Right(Unit)
    }

    protected abstract fun validateFields(params: Params): List<Either<SignInFailure.WrongInput, String>>

    protected abstract suspend fun validateEnabledAuthMethod(params: Params): Either<Failure, Unit>

    protected suspend fun getSignInMethodsForEmail(email: String): Either<Failure, List<SignInMethod>> {
        return userRepository.getSignInMethodsForEmail(email)
    }

    protected abstract suspend fun signInRepository(params: Params): Either<Failure, Unit>

}