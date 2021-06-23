package com.diegoparra.veggie.auth.ui_utils

import android.widget.TextView
import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.core.kotlin.Resource
import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.handleAuthError(
    resource: Resource<String>,
    fieldName: String = this.hint.toString(),
    femaleGenderString: Boolean = false,
    wrongInputFailureMessage: (failure: AuthFailure.WrongInput) -> String =
        { failure -> failure.getDefaultErrorMessage(context, fieldName, femaleGenderString) },
    failureMessage: (failure: AuthFailure) -> String = { it.getDefaultErrorMessage(context) }
) {
    //this.findViewById<TextView>(R.id.textinput_error).apply { maxLines = 4 }
    error = when (resource) {
        is Resource.Success -> null
        is Resource.Error -> {
            when (val failure = resource.failure) {
                is AuthFailure.WrongInput -> wrongInputFailureMessage(failure)
                is AuthFailure -> failureMessage(failure)
                else -> failure.toString()
            }
        }
        else -> null
    }
}