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


    /*sealed class SignInFailure : Failure() {
        class FirebaseException(val exception: Exception) : SignInFailure()
        object EmptyField : SignInFailure()
        object InvalidEmail : SignInFailure()
        object NewUser : SignInFailure()
        class NotLinkedSignInMethod(val signInMethod: String, val linkedSignInMethods: List<String>) : SignInFailure()
    }*/

}

sealed class SignInFailure : Failure() {

    class FirebaseException(val exception: Exception) : SignInFailure()
    class SignInMethodNotLinked(val signInMethod: String, val linkedSignInMethods: List<String>) : SignInFailure()

    sealed class WrongInput : SignInFailure() {
        object EmptyField : WrongInput()
        object InvalidEmail : WrongInput()
    }

}