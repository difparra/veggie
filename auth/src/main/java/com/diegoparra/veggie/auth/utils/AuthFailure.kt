package com.diegoparra.veggie.auth.utils

import com.diegoparra.veggie.core.kotlin.Failure
import java.lang.Exception

sealed class AuthFailure: Failure.FeatureFailure() {

    class ServerError(val exception: Exception, override val message: String? = exception.message): AuthFailure()

    sealed class SignInState : AuthFailure() {
        object NotSignedIn : SignInState()
        object Anonymous : SignInState()
    }

    sealed class WrongSignInMethod(val email: String) : AuthFailure() {
        class NewUser(email: String) : WrongSignInMethod(email)
        class ExistentUser(email: String) : WrongSignInMethod(email)
        class SignInMethodNotLinked(
            email: String,
            val signInMethod: String,
            val linkedSignInMethods: List<String>
        ) : WrongSignInMethod(email)

        class Unknown(email: String, override val message: String) : WrongSignInMethod(email)
    }

    sealed class WrongInput(val field: String, val input: String) : AuthFailure() {
        class Empty(field: String, input: String) : WrongInput(field, input)
        class Short(field: String, input: String, val minLength: Int) : WrongInput(field, input)
        class Invalid(field: String, input: String) : WrongInput(field, input)        //  Email
        class Incorrect(field: String, input: String) :
            WrongInput(field, input)      //  Password or when authenticating

        class Unknown(field: String, input: String, override val message: String) :
            WrongInput(field, input)
    }

    class ValidationFailures(val failures: Set<AuthFailure>) : AuthFailure()



    sealed class PhoneAuthFailures: AuthFailure() {
        object InvalidRequest: PhoneAuthFailures()
        object TooManyRequests: PhoneAuthFailures()
        object InvalidSmsCode: PhoneAuthFailures()
        object ExpiredSmsCode: PhoneAuthFailures()
    }
}