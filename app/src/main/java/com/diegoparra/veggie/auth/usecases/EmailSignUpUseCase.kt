package com.diegoparra.veggie.auth.usecases

import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.auth.domain.SignInMethod
import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.auth.domain.Profile
import com.diegoparra.veggie.core.TextInputValidation
import javax.inject.Inject

class EmailSignUpUseCase @Inject constructor(
    authRepository: AuthRepository
) : EmailAuthUseCase<EmailSignUpUseCase.Params>(authRepository) {

    data class Params(
        override val email: String,
        override val password: String,
        val name: String,
    ) : EmailParams


    fun validateName(name: String): Either<SignInFailure.WrongInput, String> =
        TextInputValidation.forName(name)

    override fun validateAdditionalFields(params: Params): Set<Either<SignInFailure.WrongInput, String>> =
        setOf(validateName(params.name))


    override suspend fun validateNotEmailCollision(email: String): Either<Failure, Unit> {
        return emailCollisionValidation.isValidForSignUp(email, SignInMethod.EMAIL)
    }

    override suspend fun signInRepository(params: Params): Either<Failure, Unit> {
        val profile = Profile(
            id = "",        //  Will be actually created by the repository
            email = params.email,
            name = params.name,
            photoUrl = null
        )
        return authRepository
            .signUpWithEmailAndPassword(profile, params.password)
            .map { Unit }
    }


}