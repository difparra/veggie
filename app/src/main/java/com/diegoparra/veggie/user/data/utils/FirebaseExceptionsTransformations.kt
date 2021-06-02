package com.diegoparra.veggie.user.data.utils

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.SignInFailure
import com.diegoparra.veggie.user.entities_and_repo.SignInMethod
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.diegoparra.veggie.user.entities_and_repo.UserConstants.SignInFields.EMAIL
import com.diegoparra.veggie.user.entities_and_repo.UserConstants.SignInFields.PASSWORD

object FirebaseExceptionsTransformations {


    /**
     *      DEALING WITH FIREBASE EXCEPTIONS TO FAILURES
     *      -----------------------------------------------------------------------------
     *      As a general rule: Try to put arguments whenever possible, as they will return a more
     *      detailed Failure. If arguments are null, they will return a generic ServerError Failure.
     *
     */

    fun FirebaseAuthUserCollisionException.toFailure(signInMethod: SignInMethod, linkedSignInMethods: Either<Failure, List<SignInMethod>>): Failure {
        //  createUserWithEmailAndPassword - Already exists an account with the given email address
        return when (linkedSignInMethods) {
            is Either.Left -> linkedSignInMethods.a
            is Either.Right -> SignInFailure.WrongSignInMethod.SignInMethodNotLinked(
                email = this.email ?: "",
                signInMethod = signInMethod.toString(),
                linkedSignInMethods = linkedSignInMethods.b.map { it.toString() }
            )
        }
    }

    fun FirebaseAuthInvalidCredentialsException.toFailure(
        email: String? = null, password: String? = null
    ): Failure {
        return if (this is FirebaseAuthWeakPasswordException) {
            if (password != null) {
                SignInFailure.WrongInput.Unknown(
                    field = PASSWORD, input = password,
                    message = this.message ?: "Password is weak"
                )
            } else {
                Failure.ServerError(exception = this, message = this.message ?: "Password is weak")
            }
        } else {
            if (password != null) {
                SignInFailure.WrongInput.Incorrect(field = PASSWORD, input = password)
            } else if (email != null) {
                SignInFailure.WrongInput.Incorrect(field = EMAIL, input = email)
            } else {
                Failure.ServerError(this)
            }
        }
    }

    fun FirebaseAuthInvalidUserException.toFailure(email: String?): Failure {
        return if (email != null) {
            SignInFailure.WrongInput.Unknown(
                field = EMAIL, input = email,
                message = this.localizedMessage ?: "Email does not exists or has been disabled."
            )
        } else {
            Failure.ServerError(
                exception = this,
                message = this.message ?: "Email does not exists or has been disabled."
            )
        }
    }

}