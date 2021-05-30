package com.diegoparra.veggie.user.ui.utils

import android.content.Context
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.Resource
import com.diegoparra.veggie.core.SignInFailure
import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.handleError(
    resource: Resource<String>,
    wrongInputErrorMessage: (context: Context, field: String, failure: SignInFailure.WrongInput, femaleString: Boolean) -> String? = ::getDefaultWrongInputErrorMessage,
    wrongSignInMethodErrorMessage: (context: Context, failure: SignInFailure.WrongSignInMethod) -> String = ::getDefaultWrongSignInMethodErrorMessage,
    otherErrorMessage: (context: Context, field: String, failure: Failure, femaleString: Boolean) -> String? = { _, _, f, _ -> f.toString() },
    femaleGenderString: Boolean = false,
) {
    val fieldName = this.hint.toString().lowercase()
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

fun getDefaultWrongInputErrorMessage(
    context: Context, field: String, failure: SignInFailure.WrongInput, femaleString: Boolean
): String = with(context) {
    when (failure) {
        is SignInFailure.WrongInput.Empty -> getString(R.string.failure_empty_field)
        is SignInFailure.WrongInput.Short -> {
            if (femaleString) {
                getString(R.string.failure_short_field_f, field, failure.minLength)
            } else {
                getString(R.string.failure_short_field_m, field, failure.minLength)
            }
        }
        is SignInFailure.WrongInput.Invalid -> {
            if (femaleString) {
                getString(R.string.failure_invalid_field_f, field)
            } else {
                getString(R.string.failure_invalid_field_m, field)
            }
        }
        is SignInFailure.WrongInput.Incorrect -> {
            if (femaleString) {
                getString(R.string.failure_incorrect_field_f, field)
            } else {
                getString(R.string.failure_incorrect_field_m, field)
            }
        }
        is SignInFailure.WrongInput.Unknown -> failure.message
    }
}

fun getDefaultWrongSignInMethodErrorMessage(
    context: Context, failure: SignInFailure.WrongSignInMethod
): String = with(context) {
    when (failure) {
        is SignInFailure.WrongSignInMethod.NewUser -> getString(R.string.failure_new_user)
        is SignInFailure.WrongSignInMethod.ExistentUser -> getString(
            R.string.failure_existent_user
        )
        is SignInFailure.WrongSignInMethod.SignInMethodNotLinked -> getString(
            R.string.failure_not_linked_sign_in_method,
            failure.linkedSignInMethods.joinToString()
        )
        is SignInFailure.WrongSignInMethod.Unknown -> failure.message
    }
}