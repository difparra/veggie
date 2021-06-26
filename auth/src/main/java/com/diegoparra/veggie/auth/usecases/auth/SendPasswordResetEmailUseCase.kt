package com.diegoparra.veggie.auth.usecases.auth

import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.auth.domain.SignInMethod
import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.auth.utils.TextInputValidation
import com.diegoparra.veggie.auth.usecases.utils.EmailCollisionValidation
import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.flatMap
import timber.log.Timber
import javax.inject.Inject

class SendPasswordResetEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    private val emailCollisionValidation = EmailCollisionValidation(authRepository)

    suspend operator fun invoke(email: String): Either<AuthFailure, Unit> {
        val result = validateEmail(email)
            .flatMap { validateNotEmailCollision(email) }
            .flatMap { sendPasswordResetEmail(email) }
        Timber.d("result = $result")
        return result
    }

    private fun validateEmail(email: String): Either<AuthFailure.WrongInput, String> =
        TextInputValidation.forEmail(email)

    private suspend fun validateNotEmailCollision(email: String): Either<AuthFailure, Unit> =
        emailCollisionValidation.isValidForSignIn(email, SignInMethod.EMAIL)

    private suspend fun sendPasswordResetEmail(email: String): Either<AuthFailure, Unit> =
        authRepository.sendPasswordResetEmail(email)

}