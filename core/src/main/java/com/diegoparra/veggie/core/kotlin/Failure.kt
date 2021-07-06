package com.diegoparra.veggie.core.kotlin

import android.content.Context
import androidx.annotation.StringRes
import com.diegoparra.veggie.core.R
import java.lang.Exception

sealed class Failure(
    protected open val debugMessage: String? = null,
    @StringRes protected open val strRes: Int? = null,
    protected open val formatArgs: Array<out Any> = emptyArray()
) {
    override fun toString(): String {
        return debugMessage ?: super.toString()
    }

    open fun getContextMessage(context: Context): String {
        val contextString = strRes?.let {
            if (formatArgs.isNotEmpty()) {
                context.getString(it, formatArgs)
            } else {
                context.getString(it)
            }
        }
        return contextString ?: toString()
    }


    abstract class FeatureFailure : Failure()

    object NetworkConnection : Failure(strRes = R.string.failure_network_connection)
    class ServerError(val exception: Exception, message: String? = null) :
        Failure(debugMessage = message ?: exception.message)

    class ClientError(val exception: Exception? = null, message: String? = null) :
        Failure(debugMessage = message ?: exception?.message)

    object NotFound : Failure()
    //  Use just as 404 page not found.
    //  Only when some specific resource that is very likely to exists in database is not found.
    //  For example: User(id), variation(prodId), ...
    //  Not on emptyLists or some variables that is almost same probability they exists as they
    //  don't. As in cartFailure.

}