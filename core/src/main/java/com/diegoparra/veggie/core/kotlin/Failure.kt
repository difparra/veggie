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
    object NotFound: Failure()
        //  Use just as 404 page not found.
        //  Only when some specific resource that is very likely to exists in database is not found.
        //  For example: User(id), variation(prodId), ...
        //  Not on emptyLists or some variables that is almost same probability they exists as they
        //  don't. As in cartFailure.

}