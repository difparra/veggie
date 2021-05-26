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
    object NewUser : SignInFailure()
    object ExistentUser : SignInFailure()
    class SignInMethodNotLinked(val signInMethod: String, val linkedSignInMethods: List<String>) :
        SignInFailure()

    sealed class WrongInput : SignInFailure() {
        sealed class Email : WrongInput() {
            object Empty : Email()
            object Invalid : Email()
        }

        sealed class Password : WrongInput() {
            object Empty : Password()
            object Short : Password() {
                const val minLength = 6
            }
        }

        object NameEmpty : WrongInput()
    }

    class WrongInputList(val failures: List<WrongInput>) : SignInFailure()

}