package com.diegoparra.veggie.user.auth.usecases.auth

import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.core.TextInputValidation
import com.diegoparra.veggie.user.auth.domain.*
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


    fun validateName(name: String): Either<SignInFailure.WrongInput, String> =
        TextInputValidation.forName(name)

    override fun validateAdditionalFields(params: Params): Set<Either<SignInFailure.WrongInput, String>> =
        setOf(validateName(params.name))


    override suspend fun validateNotEmailCollision(email: String): Either<Failure, Unit> {
        return emailCollisionValidation.isValidForSignUp(email, SignInMethod.EMAIL)
    }


    //      ----------------------------------------------------------------------------------------

    override suspend fun authenticate(params: Params): Either<Failure, AuthResults> {
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