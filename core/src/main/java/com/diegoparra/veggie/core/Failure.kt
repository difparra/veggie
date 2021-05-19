package com.diegoparra.veggie.core

import java.lang.Exception

sealed class Failure {

    object NetworkConnection : Failure()
    class ServerError(val exception: Exception? = null, val message: String? = null) : Failure()


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