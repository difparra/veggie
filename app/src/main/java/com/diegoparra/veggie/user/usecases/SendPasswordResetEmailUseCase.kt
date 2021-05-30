package com.diegoparra.veggie.user.usecases

import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.user.entities_and_repo.SignInMethod
import com.diegoparra.veggie.user.entities_and_repo.UserRepository
import com.diegoparra.veggie.user.usecases.utils.EmailCollisionValidation
import com.diegoparra.veggie.user.usecases.utils.TextInputValidation
import timber.log.Timber
import javax.inject.Inject

class SendPasswordResetEmailUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    private val emailCollisionValidation = EmailCollisionValidation(userRepository)


    suspend operator fun invoke(email: String) : Either<Failure, Unit> {
        val result = TextInputValidation.forEmail(email)
            .suspendFlatMap {
                emailCollisionValidation.isValidForSignIn(email, SignInMethod.EMAIL)
            }
            .suspendFlatMap {
                userRepository.sendPasswordResetEmail(email)
            }
        Timber.d("result = $result")
        return result
    }

}