package com.diegoparra.veggie.auth.usecases.auth

import com.diegoparra.veggie.auth.domain.*
import com.diegoparra.veggie.auth.usecases.utils.EmailCollisionValidation
import com.diegoparra.veggie.auth.utils.AuthCallbacks
import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.input_validation.TextInputValidation
import com.diegoparra.veggie.core.kotlin.input_validation.InputFailure
import com.diegoparra.veggie.core.kotlin.onFailure
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

    override suspend fun validate(params: Params): Either<InputFailure.InputFailuresList, Unit> {
        val validationFailures = mutableSetOf<InputFailure>()

        validateEmailInput(params.email).onFailure {
            validationFailures.add(it)
        }
        validatePasswordInput(params.password).onFailure {
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
        TextInputValidation.validateNotEmpty(
            str = password,
            field = InputFailure.Companion.Field.PASSWORD
        )


    override suspend fun additionalValidations(params: Params): Either<Failure, Unit> {
        //  Validate not email collision
        return emailCollisionValidation.isValidForSignIn(
            email = params.email,
            signInMethod = SignInMethod.EMAIL
        )
    }


    //      ----------------------------------------------------------------------------------------

    override suspend fun authenticate(params: Params): Either<Failure, AuthResults> {
        return authRepository.signInWithEmailAndPassword(params.email, params.password)
    }


}