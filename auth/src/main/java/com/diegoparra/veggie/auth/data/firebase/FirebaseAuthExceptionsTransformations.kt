package com.diegoparra.veggie.auth.data.firebase

import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.auth.domain.SignInMethod
import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.input_validation.InputFailure
import com.diegoparra.veggie.core.kotlin.input_validation.InputFailure.Companion.Field
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
        linkedSignInMethods: Either<Failure, List<SignInMethod>>
    ): Failure {
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
    ): Failure {
        return if (this is FirebaseAuthWeakPasswordException) {
            if (password != null) {
                InputFailure.Unknown(
                    field = Field.PASSWORD, input = password,
                    debugMessage = this.message ?: "Password is weak"
                )
            } else {
                Failure.ServerError(exception = this, message = this.message ?: "Password is weak")
            }
        } else {
            if (password != null) {
                InputFailure.Incorrect(field = Field.PASSWORD, input = password)
            } else if (email != null) {
                InputFailure.Incorrect(field = Field.EMAIL, input = email)
            } else {
                Failure.ServerError(this)
            }
        }
    }

    fun FirebaseAuthInvalidUserException.toFailure(email: String?): Failure {
        return if (email != null) {
            InputFailure.Unknown(
                field = Field.EMAIL, input = email,
                debugMessage = this.localizedMessage
                    ?: "Email does not exists or has been disabled."
            )
        } else {
            Failure.ServerError(
                exception = this,
                message = this.message ?: "Email does not exists or has been disabled."
            )
        }
    }

}