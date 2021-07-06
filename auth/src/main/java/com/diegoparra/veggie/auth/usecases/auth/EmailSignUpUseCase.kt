package com.diegoparra.veggie.auth.usecases.auth

import com.diegoparra.veggie.auth.domain.*
import com.diegoparra.veggie.auth.usecases.utils.EmailCollisionValidation
import com.diegoparra.veggie.auth.utils.AuthCallbacks
import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.input_validation.TextInputValidation
import com.diegoparra.veggie.core.kotlin.input_validation.InputFailure
import com.diegoparra.veggie.core.kotlin.onFailure
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


    override suspend fun validate(params: Params): Either<InputFailure.InputFailuresList, Unit> {
        Timber.d("validate() called with: params = $params")
        val validationFailures = mutableSetOf<InputFailure>()

        validateEmailInput(params.email).onFailure {
            validationFailures.add(it)
        }
        validatePasswordInput(params.password).onFailure {
            validationFailures.add(it)
        }
        validateNameInput(params.name).onFailure {
            validationFailures.add(it)
        }

        return if (validationFailures.isEmpty()) {
            Either.Right(Unit)
        } else {
            Either.Left(InputFailure.InputFailuresList(validationFailures))
        }
    }

    private fun validateEmailInput(email: String): Either<InputFailure, String> =
        TextInputValidation.forEmail(email)

    fun validatePasswordInput(password: String) =
        TextInputValidation.forPassword(password)

    fun validateNameInput(name: String) =
        TextInputValidation.forName(name)


    override suspend fun additionalValidations(params: Params): Either<Failure, Unit> {
        return emailCollisionValidation.isValidForSignUp(
            email = params.email,
            signInMethod = SignInMethod.EMAIL
        )
    }


    //      ----------------------------------------------------------------------------------------

    override suspend fun authenticate(params: Params): Either<Failure, AuthResults> {
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