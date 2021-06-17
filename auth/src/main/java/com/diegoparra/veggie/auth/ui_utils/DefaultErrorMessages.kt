package com.diegoparra.veggie.auth.ui_utils

import android.content.Context
import com.diegoparra.veggie.auth.R
import com.diegoparra.veggie.auth.utils.AuthFailure

fun AuthFailure.getDefaultErrorMessage(context: Context): String {
    return when (this) {
        is AuthFailure.ServerError -> this.getDefaultErrorMessage(context)
        is AuthFailure.SignInState -> this.getDefaultErrorMessage(context)
        is AuthFailure.WrongSignInMethod -> this.getDefaultErrorMessage(context)
        is AuthFailure.WrongInput -> this.getDefaultErrorMessage(context)
        is AuthFailure.ValidationFailures -> "Se han encontrado los siguientes errores: ${
            this.getDefaultErrorMessages(
                context
            ).joinToString("\n")
        }"
        is AuthFailure.PhoneAuthFailures -> this.getDefaultErrorMessage(context)
    }
}


fun AuthFailure.ServerError.getDefaultErrorMessage(context: Context): String {
    return message
        ?: exception.message
        ?: context.getString(R.string.failure_auth_generic)
}

fun AuthFailure.SignInState.getDefaultErrorMessage(context: Context): String {
    return when (this) {
        AuthFailure.SignInState.NotSignedIn -> context.getString(R.string.failure_not_signed_in)
        AuthFailure.SignInState.Anonymous -> context.getString(R.string.failure_not_signed_in)
    }
}

fun AuthFailure.WrongSignInMethod.getDefaultErrorMessage(context: Context): String {
    return when (this) {
        is AuthFailure.WrongSignInMethod.NewUser -> context.getString(R.string.failure_new_user)
        is AuthFailure.WrongSignInMethod.ExistentUser -> context.getString(
            R.string.failure_existent_user
        )
        is AuthFailure.WrongSignInMethod.SignInMethodNotLinked -> context.getString(
            R.string.failure_not_linked_sign_in_method,
            this.linkedSignInMethods.joinToString()
        )
        is AuthFailure.WrongSignInMethod.Unknown -> this.message
    }
}

fun AuthFailure.WrongInput.getDefaultErrorMessage(
    context: Context,
    field: String? = null,
    femaleString: Boolean = false
): String {
    val fieldName = field?.lowercase() ?: this.field
    return when (this) {
        is AuthFailure.WrongInput.Empty -> context.getString(R.string.failure_empty_field)
        is AuthFailure.WrongInput.Short -> {
            if (femaleString) {
                context.getString(R.string.failure_short_field_f, fieldName, this.minLength)
            } else {
                context.getString(R.string.failure_short_field_m, fieldName, this.minLength)
            }
        }
        is AuthFailure.WrongInput.Invalid -> {
            if (femaleString) {
                context.getString(R.string.failure_invalid_field_f, fieldName)
            } else {
                context.getString(R.string.failure_invalid_field_m, fieldName)
            }
        }
        is AuthFailure.WrongInput.Incorrect -> {
            if (femaleString) {
                context.getString(R.string.failure_incorrect_field_f, fieldName)
            } else {
                context.getString(R.string.failure_incorrect_field_m, fieldName)
            }
        }
        is AuthFailure.WrongInput.Unknown -> this.message
    }
}

fun AuthFailure.ValidationFailures.getDefaultErrorMessages(context: Context): List<String> {
    return this.failures.map {
        it.getDefaultErrorMessage(context)
    }
}

fun AuthFailure.PhoneAuthFailures.getDefaultErrorMessage(context: Context): String {
    return when (this) {
        AuthFailure.PhoneAuthFailures.InvalidRequest -> context.getString(R.string.failure_invalid_request_phone_number)
        AuthFailure.PhoneAuthFailures.ExpiredSmsCode -> context.getString(R.string.failure_code_has_expired)
        AuthFailure.PhoneAuthFailures.InvalidSmsCode -> context.getString(
            R.string.failure_incorrect_field_m,
            context.getString(R.string.sms_code)
        )
        AuthFailure.PhoneAuthFailures.TooManyRequests -> context.getString(R.string.failure_too_many_requests)
    }
}