package com.diegoparra.veggie.auth.usecases

import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.auth.domain.SignInMethod
import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.auth.domain.AuthResults
import com.diegoparra.veggie.auth.domain.Profile
import com.diegoparra.veggie.core.TextInputValidation
import com.diegoparra.veggie.user.domain.UserRepository
import javax.inject.Inject

class EmailSignInUseCase @Inject constructor(
    authRepository: AuthRepository,
    userRepository: UserRepository
) : EmailAuthUseCase<EmailSignInUseCase.Params>(authRepository, userRepository) {

    data class Params(
        override val email: String,
        override val password: String
    ) : EmailParams


    override fun validatePassword(password: String): Either<SignInFailure.WrongInput, String> {
        val validation = TextInputValidation.forPassword(password)
        return if (validation is Either.Left && validation.a is SignInFailure.WrongInput.Short) {
            Either.Right(password)
        } else {
            validation
        }
    }

    override fun validateAdditionalFields(params: Params): Set<Either<SignInFailure.WrongInput, String>> =
        setOf()


    override suspend fun validateNotEmailCollision(email: String): Either<Failure, Unit> {
        return emailCollisionValidation.isValidForSignIn(email, SignInMethod.EMAIL)
    }

    //      ----------------------------------------------------------------------------------------

    override suspend fun signIn(params: Params): Either<Failure, AuthResults> {
        return authRepository.signInWithEmailAndPassword(params.email, params.password)
    }

    //      ----------------------------------------------------------------------------------------

    override suspend fun saveData(profile: Profile, isNewUser: Boolean): Either<Failure, Unit> {
        //  There is no need to update firestore data when signing in, as data should already be
        //  there user has previously signed up.
        return Either.Right(Unit)
    }

}