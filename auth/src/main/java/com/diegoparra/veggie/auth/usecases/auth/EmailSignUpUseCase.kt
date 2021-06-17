package com.diegoparra.veggie.auth.usecases.auth

import com.diegoparra.veggie.auth.domain.*
import com.diegoparra.veggie.auth.usecases.utils.EmailCollisionValidation
import com.diegoparra.veggie.auth.utils.AuthCallbacks
import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.auth.utils.TextInputValidation
import com.diegoparra.veggie.core.kotlin.Either
import timber.log.Timber
import javax.inject.Inject

class EmailSignUpUseCase @Inject constructor(
    authRepository: AuthRepository,
    authCallbacks: AuthCallbacks
) : AuthUseCase<EmailSignUpUseCase.Params>(authRepository, authCallbacks) {

    private val emailCollisionValidation by lazy { EmailCollisionValidation(authRepository) }

    data class Params(
        val email: String,
        val password: String,
        val name: String,
    )


    override suspend fun validate(params: Params): Either<AuthFailure.ValidationFailures, Unit> {
        Timber.d("validate() called with: params = $params")
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

        val nameInputValidation = validateNameInput(params.name)
        if (nameInputValidation is Either.Left) {
            validationFailures.add(nameInputValidation.a)
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
        TextInputValidation.forPassword(password)

    fun validateNameInput(name: String) =
        TextInputValidation.forName(name)

    private suspend fun validateNotEmailCollision(email: String): Either<AuthFailure, Unit> {
        return emailCollisionValidation.isValidForSignUp(
            email = email,
            signInMethod = SignInMethod.EMAIL
        )
    }


    //      ----------------------------------------------------------------------------------------

    override suspend fun authenticate(params: Params): Either<AuthFailure, AuthResults> {
        Timber.d("authenticate() called with: params = $params")
        val profile = Profile(
            id = "",        //  Will be actually created by the repository
            email = params.email,
            name = params.name,
            photoUrl = null,
            phoneNumber = null
        )
        return authRepository.signUpWithEmailAndPassword(profile, params.password)
    }


}