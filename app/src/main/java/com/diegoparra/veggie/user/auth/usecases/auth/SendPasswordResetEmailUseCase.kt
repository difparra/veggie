package com.diegoparra.veggie.user.auth.usecases.auth

import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.user.auth.domain.SignInMethod
import com.diegoparra.veggie.user.auth.domain.AuthRepository
import com.diegoparra.veggie.user.auth.usecases.utils.EmailCollisionValidation
import com.diegoparra.veggie.core.TextInputValidation
import timber.log.Timber
import javax.inject.Inject

class SendPasswordResetEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    private val emailCollisionValidation = EmailCollisionValidation(authRepository)

    suspend operator fun invoke(email: String): Either<Failure, Unit> {
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