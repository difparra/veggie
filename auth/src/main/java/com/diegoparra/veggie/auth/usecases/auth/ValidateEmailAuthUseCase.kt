package com.diegoparra.veggie.auth.usecases.auth

import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.auth.domain.SignInMethod
import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.input_validation.TextInputValidation
import com.diegoparra.veggie.core.kotlin.flatMap
import com.diegoparra.veggie.core.kotlin.input_validation.InputFailure
import com.diegoparra.veggie.core.kotlin.onFailure
import timber.log.Timber
import javax.inject.Inject

class ValidateEmailAuthUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(email: String): Either<Failure, EmailAuthMethod> {
        validateEmailInput(email).onFailure {
            return Either.Left(it)
        }
        return validateEmailCollision(email)
    }


    fun validateEmailInput(email: String): Either<InputFailure, String> =
        TextInputValidation.forEmail(email)

    private suspend fun validateEmailCollision(email: String): Either<Failure, EmailAuthMethod> {
        return getSignInMethodsForEmail(email).flatMap {
            Timber.d("email: $email - signInMethodsList: ${it.joinToString()}")
            if (it.isEmpty()) {
                Either.Right(EmailAuthMethod(email = email, isNewUser = true))
            } else if (SignInMethod.EMAIL in it) {
                Either.Right(EmailAuthMethod(email = email, isNewUser = false))
            } else {
                Either.Left(
                    AuthFailure.WrongSignInMethod.SignInMethodNotLinked(
                        email = email,
                        signInMethod = SignInMethod.EMAIL.toString(),
                        linkedSignInMethods = it.map { it.toString() })
                )
            }
        }
    }

    private suspend fun getSignInMethodsForEmail(email: String): Either<Failure, List<SignInMethod>> {
        return authRepository.getSignInMethodsForEmail(email)
    }


    class EmailAuthMethod(
        val email: String,
        val isNewUser: Boolean
    )

}