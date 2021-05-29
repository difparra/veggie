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

}


sealed class SignInFailure : Failure() {

    sealed class SignInState : SignInFailure() {
        object NotSignedIn : SignInState()
        object Anonymous : SignInState()
    }

    sealed class WrongSignInMethod : SignInFailure() {
        object NewUser : WrongSignInMethod()
        object ExistentUser : WrongSignInMethod()
        class SignInMethodNotLinked(
            val signInMethod: String,
            val linkedSignInMethods: List<String>
        ) : WrongSignInMethod()

        class Unknown(override val message: String) : WrongSignInMethod()
    }

    sealed class WrongInput(val field: String) : SignInFailure() {
        class Empty(field: String) : WrongInput(field)
        class Short(field: String, val minLength: Int) : WrongInput(field)
        class Invalid(field: String) : WrongInput(field)        //  Email
        class Incorrect(field: String) : WrongInput(field)      //  Password or when authenticating
        class Unknown(field: String, override val message: String) : WrongInput(field)
    }

    class ValidationFailures(val failures: Set<Failure>) : SignInFailure()

}