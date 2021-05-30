package com.diegoparra.veggie.user.usecases

import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.user.entities_and_repo.SignInMethod
import com.diegoparra.veggie.user.entities_and_repo.UserRepository
import com.diegoparra.veggie.user.usecases.utils.TextInputValidation
import timber.log.Timber
import javax.inject.Inject

class EmailSignInUseCase @Inject constructor(
    userRepository: UserRepository
) : EmailAuthUseCase<EmailSignInUseCase.Params>(userRepository) {

    data class Params(
        override val email: String,
        override val password: String
    ) : EmailParams


    override fun validatePassword(password: String): Either<SignInFailure.WrongInput, String> {
        val validation = TextInputValidation.forPassword(password)
        return if (validation is Either.Left && validation.a is SignInFailure.WrongInput.Short) {
            Either.Right(password)
        } else {
            validation
        }
    }

    override fun validateAdditionalFields(params: Params): Set<Either<SignInFailure.WrongInput, String>> =
        setOf()


    override suspend fun validateNotEmailCollision(email: String): Either<Failure, Unit> {
        return emailCollisionValidation.isValidForSignIn(email, SignInMethod.EMAIL)
    }


    override suspend fun signInRepository(params: Params): Either<Failure, Unit> {
        return userRepository.signInWithEmailAndPassword(params.email, params.password)
    }

}