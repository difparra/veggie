package com.diegoparra.veggie.auth.usecases.utils

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.SignInFailure
import com.diegoparra.veggie.core.flatMap
import com.diegoparra.veggie.auth.domain.SignInMethod
import com.diegoparra.veggie.auth.domain.AuthRepository
import timber.log.Timber

class EmailCollisionValidation(
    private val authRepository: AuthRepository
) {

    suspend fun isValidForSignIn(email: String, signInMethod: SignInMethod): Either<Failure, Unit> {
        return getSignInMethodsForEmail(email).flatMap {
            Timber.d("email: $email - signInMethodsList: ${it.joinToString()}")
            if (signInMethod in it) {
                Either.Right(Unit)
            } else if (it.isEmpty()) {
                Either.Left(SignInFailure.WrongSignInMethod.NewUser(email = email))
            } else {
                Either.Left(
                    SignInFailure.WrongSignInMethod.SignInMethodNotLinked(
                        email = email,
                        signInMethod = signInMethod.toString(),
                        linkedSignInMethods = it.map { it.toString() })
                )
            }
        }
    }

    suspend fun isValidForSignUp(email: String, signInMethod: SignInMethod): Either<Failure, Unit> {
        return getSignInMethodsForEmail(email).flatMap {
            Timber.d("email: $email - signInMethodsList: ${it.joinToString()}")
            if (it.isEmpty()) {
                Either.Right(Unit)
            } else if (signInMethod in it) {
                Either.Left(SignInFailure.WrongSignInMethod.ExistentUser(email = email))
            } else {
                Either.Left(
                    SignInFailure.WrongSignInMethod.SignInMethodNotLinked(
                        email = email,
                        signInMethod = signInMethod.toString(),
                        linkedSignInMethods = it.map { it.toString() })
                )
            }
        }
    }


    private suspend fun getSignInMethodsForEmail(email: String): Either<Failure, List<SignInMethod>> {
        return authRepository.getSignInMethodsForEmail(email)
    }


}