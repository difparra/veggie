package com.diegoparra.veggie.auth.usecases.auth

import com.diegoparra.veggie.auth.domain.*
import com.diegoparra.veggie.auth.usecases.utils.EmailCollisionValidation
import com.diegoparra.veggie.auth.utils.AuthCallbacks
import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.auth.utils.Fields
import com.diegoparra.veggie.auth.utils.TextInputValidation
import com.diegoparra.veggie.core.kotlin.Either
import javax.inject.Inject

class EmailSignInUseCase @Inject constructor(
    authRepository: AuthRepository,
    authCallbacks: AuthCallbacks
) : AuthUseCase<EmailSignInUseCase.Params>(authRepository, authCallbacks) {

    private val emailCollisionValidation by lazy { EmailCollisionValidation(authRepository) }

    data class Params(
        val email: String,
        val password: String
    )

    override suspend fun validate(params: Params): Either<AuthFailure.ValidationFailures, Unit> {
        val validationFailures = mutableSetOf<AuthFailure>()

        val emailInputValidation = validateEmailInput(params.email)
        if (emailInputValidation is Either.Left) {
            validationFailures.add(emailInputValidation.a)
        } else {
            val emailCollisionValidation = validateNotEmailCollision(params.email)
            if (emailCollisionValidation is Either.Left) {
                validationFailures.add(emailCollisionValidation.a)
            }
        }

        val passwordInputValidation = validatePasswordInput(params.password)
        if (passwordInputValidation is Either.Left) {
            validationFailures.add(passwordInputValidation.a)
        }

        return if (validationFailures.isEmpty()) {
            Either.Right(Unit)
        } else {
            Either.Left(AuthFailure.ValidationFailures(validationFailures))
        }
    }

    private fun validateEmailInput(email: String): Either<AuthFailure.WrongInput, String> =
        TextInputValidation.forEmail(email)

    fun validatePasswordInput(password: String) =
        TextInputValidation.validateNotEmpty(str = password, field = Fields.PASSWORD)

    private suspend fun validateNotEmailCollision(email: String): Either<AuthFailure, Unit> {
        return emailCollisionValidation.isValidForSignIn(
            email = email,
            signInMethod = SignInMethod.EMAIL
        )
    }


    //      ----------------------------------------------------------------------------------------

    override suspend fun authenticate(params: Params): Either<AuthFailure, AuthResults> {
        return authRepository.signInWithEmailAndPassword(params.email, params.password)
    }


}