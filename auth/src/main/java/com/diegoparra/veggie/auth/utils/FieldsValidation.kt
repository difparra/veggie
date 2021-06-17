package com.diegoparra.veggie.auth.utils

import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.validateEmail
import timber.log.Timber

object Fields {
    const val EMAIL = "email"
    const val PASSWORD = "password"
    const val NAME = "name"
    const val PHONE_NUMBER = "phone_number"
    const val ADDRESS = "address"
}

object TextInputValidation {

    private val possibleFields = listOf(Fields.EMAIL, Fields.PASSWORD, Fields.NAME, Fields.PASSWORD, Fields.ADDRESS)
    fun validateNotEmpty(str: String, field: String): Either<AuthFailure.WrongInput, String> {
        if(!possibleFields.contains(field)) {
            Timber.w("$field is not in ${possibleFields.joinToString(", ")}")
        }
        return if(str.isEmpty()) {
            Either.Left(AuthFailure.WrongInput.Empty(field = field, input = str))
        } else {
            Either.Right(str)
        }
    }

    fun forEmail(email: String): Either<AuthFailure.WrongInput, String> {
        val emailField = Fields.EMAIL
        return if (email.isEmpty()) {
            Either.Left(AuthFailure.WrongInput.Empty(field = emailField, input = email))
        } else if (!validateEmail(email)) {
            Either.Left(AuthFailure.WrongInput.Invalid(field = emailField, input = email))
        } else {
            Either.Right(email)
        }
    }

    fun forPassword(password: String): Either<AuthFailure.WrongInput, String> {
        val passwordField = Fields.PASSWORD
        return if (password.isEmpty()) {
            Either.Left(AuthFailure.WrongInput.Empty(field = passwordField, input = password))
        } else if (password.length < 6) {
            Either.Left(
                AuthFailure.WrongInput.Short(
                    field = passwordField,
                    input = password,
                    minLength = 6
                )
            )
        } else {
            Either.Right(password)
        }
    }

    fun forName(name: String): Either<AuthFailure.WrongInput, String> {
        return if (name.isEmpty()) {
            Either.Left(AuthFailure.WrongInput.Empty(field = Fields.NAME, input = name))
        } else {
            Either.Right(name)
        }
    }

    fun forPhoneNumber(phoneNumber: String): Either<AuthFailure.WrongInput, String> {
        Timber.d("phoneNumber = $phoneNumber, phoneNumberLength = ${phoneNumber.length}")
        return if (phoneNumber.isEmpty()) {
            Either.Left(AuthFailure.WrongInput.Empty(field = Fields.PHONE_NUMBER, input = phoneNumber))
        } else if (
            !(phoneNumber.startsWith("+57 3") || phoneNumber.startsWith("+573"))
            || phoneNumber.length != 13
        ) {
            Either.Left(AuthFailure.WrongInput.Invalid(field = phoneNumber, input = phoneNumber))
        } else {
            Either.Right(phoneNumber)
        }
    }

    fun forAddress(address: String): Either<AuthFailure.WrongInput, String> {
        return if(address.isBlank()) {
            Either.Left(AuthFailure.WrongInput.Empty(field = Fields.ADDRESS, input = address))
        } else {
            Either.Right(address)
        }
    }

}