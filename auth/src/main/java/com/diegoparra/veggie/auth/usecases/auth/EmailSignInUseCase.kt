package com.diegoparra.veggie.auth.usecases.auth

import com.diegoparra.veggie.auth.domain.*
import com.diegoparra.veggie.auth.utils.AuthCallbacks
import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.auth.utils.TextInputValidation
import com.diegoparra.veggie.core.kotlin.Either
import javax.inject.Inject

class EmailSignInUseCase @Inject constructor(
    authRepository: AuthRepository,
    authCallbacks: AuthCallbacks
) : EmailAuthUseCase<EmailSignInUseCase.Params>(authRepository, authCallbacks) {

    data class Params(
        override val email: String,
        override val password: String
    ) : EmailParams


    override fun validatePassword(password: String): Either<AuthFailure.WrongInput, String> {
        val validation = TextInputValidation.forPassword(password)
        return if (validation is Either.Left && validation.a is AuthFailure.WrongInput.Short) {
            Either.Right(password)
        } else {
            validation
        }
    }

    override fun validateAdditionalFields(params: Params): Set<Either<AuthFailure.WrongInput, String>> =
        setOf()


    override suspend fun validateNotEmailCollision(email: String): Either<AuthFailure, Unit> {
        return emailCollisionValidation.isValidForSignIn(email, SignInMethod.EMAIL)
    }

    //      ----------------------------------------------------------------------------------------

    override suspend fun authenticate(params: Params): Either<AuthFailure, AuthResults> {
        return authRepository.signInWithEmailAndPassword(params.email, params.password)
    }

}