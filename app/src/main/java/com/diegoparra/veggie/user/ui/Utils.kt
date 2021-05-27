package com.diegoparra.veggie.user.ui

import android.content.Context
import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.Resource
import com.diegoparra.veggie.core.SignInFailure
import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.handleError(
    resource: Resource<String>,
    inputErrorMessage: (String, SignInFailure.WrongInput, Boolean) -> String? = context.handleGenericInputError,
    otherErrorMessage: (String, Failure, Boolean) -> String? = { _, f,_ -> f.toString() },
    femaleGenderString: Boolean = false,
) {
    val fieldName = this.hint.toString().lowercase()
    error = when (resource) {
        is Resource.Success -> null
        is Resource.Error -> {
            when (val failure = resource.failure) {
                is SignInFailure.WrongInput -> inputErrorMessage(fieldName, failure, femaleGenderString)
                else -> otherErrorMessage(fieldName, failure, femaleGenderString)
            }
        }
        else -> null
    }
}

val Context.handleGenericInputError: (String, SignInFailure.WrongInput, Boolean) -> String
    get() = { field, failure, femaleString ->
        when(failure){
            is SignInFailure.WrongInput.Empty -> getString(R.string.failure_empty_field)
            is SignInFailure.WrongInput.Invalid -> {
                if(femaleString){
                    getString(R.string.failure_invalid_field_f, field)
                }else{
                    getString(R.string.failure_invalid_field_m, field)
                }
            }
            is SignInFailure.WrongInput.Short -> {
                if(femaleString){
                    getString(R.string.failure_short_field_f, field, failure.minLength)
                }else{
                    getString(R.string.failure_short_field_m, field, failure.minLength)
                }
            }
        }
    }