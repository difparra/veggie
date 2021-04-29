package com.diegoparra.veggie.core

sealed class Failure {

    object NetworkConnection : Failure()
    object ServerError : Failure()

//    /** * Extend this class for feature specific failures.*/
//    abstract class FeatureFailure: Failure()

    sealed class ProductsFailure() : Failure(){
        object TagsNotFound : ProductsFailure()
        object ProductsNotFound : ProductsFailure()
    }

    sealed class SearchFailure() : Failure(){
        object EmptyQuery : SearchFailure()
        object NoSearchResults : SearchFailure()
    }

}