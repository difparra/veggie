package com.diegoparra.veggie.auth.usecases.auth

import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.auth.domain.SignInMethod
import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.auth.utils.TextInputValidation
import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.flatMap
import timber.log.Timber
import javax.inject.Inject

class ValidateEmailAuthUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(email: String): Either<AuthFailure, EmailAuthMethod> {
        validateEmailInput(email).let {
            if(it is Either.Left) {
                return it
            }
        }
        return validateEmailCollision(email)
    }


    fun validateEmailInput(email: String): Either<AuthFailure.WrongInput, String> =
        TextInputValidation.forEmail(email)

    private suspend fun validateEmailCollision(email: String): Either<AuthFailure, EmailAuthMethod> {
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

    private suspend fun getSignInMethodsForEmail(email: String): Either<AuthFailure, List<SignInMethod>> {
        return authRepository.getSignInMethodsForEmail(email)
    }


    class EmailAuthMethod(
        val email: String,
        val isNewUser: Boolean
    )

}