package com.diegoparra.veggie.auth.ui.utils

import android.content.Context
import android.widget.TextView
import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.Resource
import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.handleError(
    resource: Resource<String>,
    wrongInputErrorMessage: (context: Context, field: String, failure: AuthFailure.WrongInput, femaleString: Boolean) -> String? = ::getDefaultWrongInputErrorMessage,
    wrongSignInMethodErrorMessage: (context: Context, failure: AuthFailure.WrongSignInMethod) -> String = ::getDefaultWrongSignInMethodErrorMessage,
    otherErrorMessage: (context: Context, field: String, failure: Failure, femaleString: Boolean) -> String? = { _, _, f, _ -> f.toString() },
    femaleGenderString: Boolean = false,
) {
    val fieldName = this.hint.toString()
    this.findViewById<TextView>(R.id.textinput_error).apply { maxLines = 4 }
    error = when (resource) {
        is Resource.Success -> null
        is Resource.Error -> {
            when (val failure = resource.failure) {
                is AuthFailure.WrongInput -> wrongInputErrorMessage(
                    context, fieldName, failure, femaleGenderString
                )
                is AuthFailure.WrongSignInMethod -> wrongSignInMethodErrorMessage(
                    context, failure
                )
                else -> otherErrorMessage(context, fieldName, failure, femaleGenderString)
            }
        }
        else -> null
    }
}