package com.diegoparra.veggie.auth.ui.utils

import android.content.Context
import com.diegoparra.veggie.auth.R
import com.diegoparra.veggie.auth.utils.AuthFailure

fun getDefaultWrongInputErrorMessage(
    context: Context, field: String, failure: AuthFailure.WrongInput, femaleString: Boolean
): String = with(context) {
    when (failure) {
        is AuthFailure.WrongInput.Empty -> getString(R.string.failure_empty_field)
        is AuthFailure.WrongInput.Short -> {
            if (femaleString) {
                getString(R.string.failure_short_field_f, field.lowercase(), failure.minLength)
            } else {
                getString(R.string.failure_short_field_m, field.lowercase(), failure.minLength)
            }
        }
        is AuthFailure.WrongInput.Invalid -> {
            if (femaleString) {
                getString(R.string.failure_invalid_field_f, field.lowercase())
            } else {
                getString(R.string.failure_invalid_field_m, field.lowercase())
            }
        }
        is AuthFailure.WrongInput.Incorrect -> {
            if (femaleString) {
                getString(R.string.failure_incorrect_field_f, field.lowercase())
            } else {
                getString(R.string.failure_incorrect_field_m, field.lowercase())
            }
        }
        is AuthFailure.WrongInput.Unknown -> failure.message
    }
}

fun getDefaultWrongSignInMethodErrorMessage(
    context: Context, failure: AuthFailure.WrongSignInMethod
): String = with(context) {
    when (failure) {
        is AuthFailure.WrongSignInMethod.NewUser -> getString(R.string.failure_new_user)
        is AuthFailure.WrongSignInMethod.ExistentUser -> getString(
            R.string.failure_existent_user
        )
        is AuthFailure.WrongSignInMethod.SignInMethodNotLinked -> getString(
            R.string.failure_not_linked_sign_in_method,
            failure.linkedSignInMethods.joinToString()
        )
        is AuthFailure.WrongSignInMethod.Unknown -> failure.message
    }
}