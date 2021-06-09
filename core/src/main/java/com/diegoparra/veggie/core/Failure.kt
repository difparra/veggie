package com.diegoparra.veggie.core

import java.lang.Exception

sealed class Failure(open val message: String? = null) {
    override fun toString(): String {
        return message ?: super.toString()
    }

    object NetworkConnection : Failure()
    class ServerError(val exception: Exception? = null, message: String? = null) :
        Failure(message = message ?: exception?.localizedMessage)

    sealed class ProductsFailure : Failure() {
        object TagsNotFound : ProductsFailure()
        object ProductsNotFound : ProductsFailure()
    }

    sealed class SearchFailure : Failure() {
        object EmptyQuery : SearchFailure()
        object NoSearchResults : SearchFailure()
    }

    sealed class CartFailure : Failure() {
        object EmptyCartList : CartFailure()
    }

    sealed class UserFailure : Failure() {
        object UserNotFound : UserFailure()
    }

    sealed class PhoneAuthFailures: Failure() {
        object InvalidRequest: PhoneAuthFailures()
        object TooManyRequests: PhoneAuthFailures()
        object InvalidSmsCode: PhoneAuthFailures()
        object ExpiredSmsCode: PhoneAuthFailures()
    }

}


sealed class SignInFailure : Failure() {

    sealed class SignInState : SignInFailure() {
        object NotSignedIn : SignInState()
        object Anonymous : SignInState()
    }

    sealed class WrongSignInMethod(val email: String) : SignInFailure() {
        class NewUser(email: String) : WrongSignInMethod(email)
        class ExistentUser(email: String) : WrongSignInMethod(email)
        class SignInMethodNotLinked(
            email: String,
            val signInMethod: String,
            val linkedSignInMethods: List<String>
        ) : WrongSignInMethod(email)

        class Unknown(email: String, override val message: String) : WrongSignInMethod(email)
    }

    sealed class WrongInput(val field: String, val input: String) : SignInFailure() {
        class Empty(field: String, input: String) : WrongInput(field, input)
        class Short(field: String, input: String, val minLength: Int) : WrongInput(field, input)
        class Invalid(field: String, input: String) : WrongInput(field, input)        //  Email
        class Incorrect(field: String, input: String) :
            WrongInput(field, input)      //  Password or when authenticating

        class Unknown(field: String, input: String, override val message: String) :
            WrongInput(field, input)
    }

    class ValidationFailures(val failures: Set<Failure>) : SignInFailure()

}