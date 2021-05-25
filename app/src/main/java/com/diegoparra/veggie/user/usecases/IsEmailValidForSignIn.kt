package com.diegoparra.veggie.user.usecases

import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.user.entities_and_repo.IsNewUser
import com.diegoparra.veggie.user.entities_and_repo.SignInMethod
import com.diegoparra.veggie.user.entities_and_repo.UserRepository
import javax.inject.Inject

class IsEmailValidForSignIn @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(
        email: String
    ): Either<Failure, IsNewUser> {
        //  Check correct format for email
        validateEmail(email)?.let {
            return Either.Left(it)
        }

        //  Operation
        return getSignInMethodsForEmail(email).flatMap {
            if (it.isEmpty()) {
                Either.Right(IsNewUser(true))
            } else if (SignInMethod.EMAIL in it) {
                Either.Right(IsNewUser(false))
            } else {
                Either.Left(
                    SignInFailure.SignInMethodNotLinked(
                        signInMethod = SignInMethod.EMAIL.toString(),
                        linkedSignInMethods = it.map { it.toString() })
                )
            }
        }
    }

    private fun validateEmail(email: String): SignInFailure.WrongInput? {
        return if (email.isEmpty()) {
            SignInFailure.WrongInput.EmptyField
        } else if (!com.diegoparra.veggie.core.validateEmail(email)) {
            SignInFailure.WrongInput.InvalidEmail
        } else {
            null
        }
    }

    private suspend fun getSignInMethodsForEmail(email: String): Either<Failure, List<SignInMethod>> {
        return userRepository.getSignInMethodsForEmail(email)
    }

}




/*
class IsUserLinkedWithSignInMethodUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(signInMethod: SignInMethod, email: String): Either<Failure, Any?> {
        //  Check correct format for email
        checkEmail(email)?.let {
            return Either.Left(it)
        }

        //  Check if user is new or if has created/linked the account with that signInMethod
        return checkSignInMethod(signInMethod, email)
    }

    private fun checkEmail(email: String): Failure? {
        return if (email.isEmpty()) {
            Failure.SignInFailure.EmptyField
        } else if (!validateEmail(email)) {
            Failure.SignInFailure.InvalidEmail
        } else {
            null
        }
    }

    private suspend fun checkSignInMethod(
        signInMethod: SignInMethod,
        email: String
    ): Either<Failure, Any?> {
        return getSignInMethodsForEmail(email).flatMap {
            if (signInMethod in it) {
                Either.Right(null)
            } else if (it.isEmpty()) {
                Either.Left(Failure.SignInFailure.NewUser)
            } else {
                Either.Left(
                    Failure.SignInFailure.NotLinkedSignInMethod(
                        signInMethod = signInMethod.toString(),
                        linkedSignInMethods = it.map { it.toString() })
                )
            }
        }
    }

    private suspend fun getSignInMethodsForEmail(email: String): Either<Failure, List<SignInMethod>> {
        return userRepository.getSignInMethodsForEmail(email)
    }

}
 */