package com.diegoparra.veggie.auth.usecases.utils

import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.flatMap
import com.diegoparra.veggie.auth.domain.SignInMethod
import com.diegoparra.veggie.auth.domain.AuthRepository
import timber.log.Timber

class EmailCollisionValidation(
    private val authRepository: AuthRepository
) {

    suspend fun isValidForSignIn(email: String, signInMethod: SignInMethod): Either<AuthFailure, Unit> {
        return getSignInMethodsForEmail(email).flatMap {
            Timber.d("email: $email - signInMethodsList: ${it.joinToString()}")
            if (signInMethod in it) {
                Either.Right(Unit)
            } else if (it.isEmpty()) {
                Either.Left(AuthFailure.WrongSignInMethod.NewUser(email = email))
            } else {
                Either.Left(
                    AuthFailure.WrongSignInMethod.SignInMethodNotLinked(
                        email = email,
                        signInMethod = signInMethod.toString(),
                        linkedSignInMethods = it.map { it.toString() })
                )
            }
        }
    }

    suspend fun isValidForSignUp(email: String, signInMethod: SignInMethod): Either<AuthFailure, Unit> {
        return getSignInMethodsForEmail(email).flatMap {
            Timber.d("email: $email - signInMethodsList: ${it.joinToString()}")
            if (it.isEmpty()) {
                Either.Right(Unit)
            } else if (signInMethod in it) {
                Either.Left(AuthFailure.WrongSignInMethod.ExistentUser(email = email))
            } else {
                Either.Left(
                    AuthFailure.WrongSignInMethod.SignInMethodNotLinked(
                        email = email,
                        signInMethod = signInMethod.toString(),
                        linkedSignInMethods = it.map { it.toString() })
                )
            }
        }
    }


    private suspend fun getSignInMethodsForEmail(email: String): Either<AuthFailure, List<SignInMethod>> {
        return authRepository.getSignInMethodsForEmail(email)
    }


}