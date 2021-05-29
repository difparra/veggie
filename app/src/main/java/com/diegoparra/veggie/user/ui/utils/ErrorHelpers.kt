package com.diegoparra.veggie.user.ui

import android.content.Context
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.Resource
import com.diegoparra.veggie.core.SignInFailure
import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.handleError(
    resource: Resource<String>,
    wrongInputErrorMessage: (field: String, failure: SignInFailure.WrongInput, femaleString: Boolean) -> String? = context.defaultWrongInputErrorMessage,
    wrongSignInMethodErrorMessage: (field: String, failure: SignInFailure.WrongSignInMethod, femaleString: Boolean) -> String = context.defaultWrongSignInMethodErrorMessage,
    otherErrorMessage: (field: String, failure: Failure, femaleString: Boolean) -> String? = { _, f, _ -> f.toString() },
    femaleGenderString: Boolean = false,
) {
    val fieldName = this.hint.toString().lowercase()
    error = when (resource) {
        is Resource.Success -> null
        is Resource.Error -> {
            when (val failure = resource.failure) {
                is SignInFailure.WrongInput -> wrongInputErrorMessage(
                    fieldName, failure, femaleGenderString
                )
                is SignInFailure.WrongSignInMethod -> wrongSignInMethodErrorMessage(
                    fieldName, failure, femaleGenderString
                )
                else -> otherErrorMessage(fieldName, failure, femaleGenderString)
            }
        }
        else -> null
    }
}

private val Context.defaultWrongInputErrorMessage: (String, SignInFailure.WrongInput, Boolean) -> String
    get() = { field, failure, femaleString ->
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

private val Context.defaultWrongSignInMethodErrorMessage: (String, SignInFailure.WrongSignInMethod, Boolean) -> String
    get() = { _, failure, _ ->
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