package com.diegoparra.veggie.user.auth.ui.utils

import android.content.Context
import android.widget.TextView
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.*
import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.handleError(
    resource: Resource<String>,
    wrongInputErrorMessage: (context: Context, field: String, failure: SignInFailure.WrongInput, femaleString: Boolean) -> String? = ::getDefaultWrongInputErrorMessage,
    wrongSignInMethodErrorMessage: (context: Context, failure: SignInFailure.WrongSignInMethod) -> String = ::getDefaultWrongSignInMethodErrorMessage,
    otherErrorMessage: (context: Context, field: String, failure: Failure, femaleString: Boolean) -> String? = { _, _, f, _ -> f.toString() },
    femaleGenderString: Boolean = false,
) {
    val fieldName = this.hint.toString()
    this.findViewById<TextView>(R.id.textinput_error).apply { maxLines = 4 }
    error = when (resource) {
        is Resource.Success -> null
        is Resource.Error -> {
            when (val failure = resource.failure) {
                is SignInFailure.WrongInput -> wrongInputErrorMessage(
                    context, fieldName, failure, femaleGenderString
                )
                is SignInFailure.WrongSignInMethod -> wrongSignInMethodErrorMessage(
                    context, failure
                )
                else -> otherErrorMessage(context, fieldName, failure, femaleGenderString)
            }
        }
        else -> null
    }
}