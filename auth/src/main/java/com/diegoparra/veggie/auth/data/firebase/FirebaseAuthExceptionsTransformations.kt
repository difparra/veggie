package com.diegoparra.veggie.auth.data.firebase

import com.diegoparra.veggie.auth.utils.Fields.EMAIL
import com.diegoparra.veggie.auth.utils.Fields.PASSWORD
import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.auth.domain.SignInMethod
import com.diegoparra.veggie.core.kotlin.Either
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

object FirebaseAuthExceptionsTransformations {


    /**
     *      DEALING WITH FIREBASE EXCEPTIONS TO FAILURES
     *      -----------------------------------------------------------------------------
     *      As a general rule: Try to put arguments whenever possible, as they will return a more
     *      detailed Failure. If arguments are null, they will return a generic ServerError Failure.
     *
     */

    fun FirebaseAuthUserCollisionException.toFailure(
        signInMethod: SignInMethod,
        linkedSignInMethods: Either<AuthFailure, List<SignInMethod>>
    ): AuthFailure {
        //  createUserWithEmailAndPassword - Already exists an account with the given email address
        return when (linkedSignInMethods) {
            is Either.Left -> linkedSignInMethods.a
            is Either.Right -> AuthFailure.WrongSignInMethod.SignInMethodNotLinked(
                email = this.email ?: "",
                signInMethod = signInMethod.toString(),
                linkedSignInMethods = linkedSignInMethods.b.map { it.toString() }
            )
        }
    }

    fun FirebaseAuthInvalidCredentialsException.toFailure(
        email: String? = null, password: String? = null
    ): AuthFailure {
        return if (this is FirebaseAuthWeakPasswordException) {
            if (password != null) {
                AuthFailure.WrongInput.Unknown(
                    field = PASSWORD, input = password,
                    message = this.message ?: "Password is weak"
                )
            } else {
                AuthFailure.ServerError(exception = this, message = this.message ?: "Password is weak")
            }
        } else {
            if (password != null) {
                AuthFailure.WrongInput.Incorrect(field = PASSWORD, input = password)
            } else if (email != null) {
                AuthFailure.WrongInput.Incorrect(field = EMAIL, input = email)
            } else {
                AuthFailure.ServerError(this)
            }
        }
    }

    fun FirebaseAuthInvalidUserException.toFailure(email: String?): AuthFailure {
        return if (email != null) {
            AuthFailure.WrongInput.Unknown(
                field = EMAIL, input = email,
                message = this.localizedMessage ?: "Email does not exists or has been disabled."
            )
        } else {
            AuthFailure.ServerError(
                exception = this,
                message = this.message ?: "Email does not exists or has been disabled."
            )
        }
    }

}