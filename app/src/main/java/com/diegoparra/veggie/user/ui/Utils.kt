package com.diegoparra.veggie.user.ui

import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.Resource
import com.google.android.material.textfield.TextInputLayout
import timber.log.Timber

fun TextInputLayout.setError(failure: Failure, mapFailureString: Map<Failure, String>) {
    if (mapFailureString.isNullOrEmpty()) {
        return
    } else {
        val message = mapFailureString[failure]
        if(message != null){
            error = message
        }else{
            error = null
            Timber.e("Failure: $failure has not assigned an error message")
        }
    }
}

fun TextInputLayout.handleError(resource: Resource<String>, mapFailureString: Map<Failure, String>) {
    when(resource){
        is Resource.Success -> error = null
        is Resource.Error -> this.setError(resource.failure, mapFailureString)
        else -> Timber.e("Not handled loading case.")
    }
}