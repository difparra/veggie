package com.diegoparra.veggie.core

import java.lang.Exception

sealed class Failure {

    object NetworkConnection : Failure()
    class ServerError(val exception: Exception? = null, val message: String? = null) : Failure()
    class FirebaseException(val exception: Exception) : Failure()

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

    class FirebaseException(val exception: Exception) : SignInFailure()

    sealed class WrongSignInMethod : SignInFailure() {
        object NewUser : WrongSignInMethod()
        object ExistentUser : WrongSignInMethod()
        class SignInMethodNotLinked(val signInMethod: String, val linkedSignInMethods: List<String>) :
            WrongSignInMethod()
    }

    sealed class WrongInput: SignInFailure() {
        object Empty : WrongInput()
        object Invalid : WrongInput()
        class Short(val minLength: Int) : WrongInput()
    }

    class WrongInputs(val inputErrors: Map<String, WrongInput>) : SignInFailure()

}