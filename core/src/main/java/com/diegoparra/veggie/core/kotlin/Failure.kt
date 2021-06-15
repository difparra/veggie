package com.diegoparra.veggie.core.kotlin

import java.lang.Exception

sealed class Failure(open val message: String? = null) {
    override fun toString(): String {
        return message ?: super.toString()
    }

    abstract class FeatureFailure: Failure()
    
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

}