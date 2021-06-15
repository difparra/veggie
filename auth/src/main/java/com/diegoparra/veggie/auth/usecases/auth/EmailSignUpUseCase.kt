package com.diegoparra.veggie.auth.usecases.auth

import com.diegoparra.veggie.auth.domain.*
import com.diegoparra.veggie.auth.utils.AuthCallbacks
import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.auth.utils.TextInputValidation
import com.diegoparra.veggie.core.kotlin.Either
import javax.inject.Inject

class EmailSignUpUseCase @Inject constructor(
    authRepository: AuthRepository,
    authCallbacks: AuthCallbacks
) : EmailAuthUseCase<EmailSignUpUseCase.Params>(authRepository, authCallbacks) {

    data class Params(
        override val email: String,
        override val password: String,
        val name: String,
    ) : EmailParams


    fun validateName(name: String): Either<AuthFailure.WrongInput, String> =
        TextInputValidation.forName(name)

    override fun validateAdditionalFields(params: Params): Set<Either<AuthFailure.WrongInput, String>> =
        setOf(validateName(params.name))


    override suspend fun validateNotEmailCollision(email: String): Either<AuthFailure, Unit> {
        return emailCollisionValidation.isValidForSignUp(email, SignInMethod.EMAIL)
    }


    //      ----------------------------------------------------------------------------------------

    override suspend fun authenticate(params: Params): Either<AuthFailure, AuthResults> {
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