package com.diegoparra.veggie.auth.usecases.auth

import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.auth.domain.SignInMethod
import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.auth.utils.TextInputValidation
import com.diegoparra.veggie.auth.usecases.utils.EmailCollisionValidation
import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.suspendFlatMap
import timber.log.Timber
import javax.inject.Inject

class SendPasswordResetEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    private val emailCollisionValidation = EmailCollisionValidation(authRepository)

    suspend operator fun invoke(email: String): Either<AuthFailure, Unit> {
        val result = TextInputValidation.forEmail(email)
            .suspendFlatMap {
                emailCollisionValidation.isValidForSignIn(email, SignInMethod.EMAIL)
            }
            .suspendFlatMap {
                authRepository.sendPasswordResetEmail(email)
            }
        Timber.d("result = $result")
        return result
    }

}