package com.diegoparra.veggie.core

import timber.log.Timber

object Fields {
    const val EMAIL = "email"
    const val PASSWORD = "password"
    const val NAME = "name"
    const val PHONE_NUMBER = "phone_number"
    const val ADDRESS = "address"
}

object TextInputValidation {

    fun forEmail(email: String): Either<SignInFailure.WrongInput, String> {
        val emailField = Fields.EMAIL
        return if (email.isEmpty()) {
            Either.Left(SignInFailure.WrongInput.Empty(field = emailField, input = email))
        } else if (!validateEmail(email)) {
            Either.Left(SignInFailure.WrongInput.Invalid(field = emailField, input = email))
        } else {
            Either.Right(email)
        }
    }

    fun forPassword(password: String): Either<SignInFailure.WrongInput, String> {
        val passwordField = Fields.PASSWORD
        return if (password.isEmpty()) {
            Either.Left(SignInFailure.WrongInput.Empty(field = passwordField, input = password))
        } else if (password.length < 6) {
            Either.Left(
                SignInFailure.WrongInput.Short(
                    field = passwordField,
                    input = password,
                    minLength = 6
                )
            )
        } else {
            Either.Right(password)
        }
    }

    fun forName(name: String): Either<SignInFailure.WrongInput, String> {
        return if (name.isEmpty()) {
            Either.Left(SignInFailure.WrongInput.Empty(field = Fields.NAME, input = name))
        } else {
            Either.Right(name)
        }
    }

    fun forPhoneNumber(phoneNumber: String): Either<SignInFailure.WrongInput, String> {
        Timber.d("phoneNumber = $phoneNumber, phoneNumberLength = ${phoneNumber.length}")
        return if (phoneNumber.isEmpty()) {
            Either.Left(SignInFailure.WrongInput.Empty(field = Fields.PHONE_NUMBER, input = phoneNumber))
        } else if (
            !(phoneNumber.startsWith("+57 3") || phoneNumber.startsWith("+573"))
            || phoneNumber.length != 13
        ) {
            Either.Left(SignInFailure.WrongInput.Invalid(field = phoneNumber, input = phoneNumber))
        } else {
            Either.Right(phoneNumber)
        }
    }

    fun forAddress(address: String): Either<SignInFailure.WrongInput, String> {
        return if(address.isBlank()) {
            Either.Left(SignInFailure.WrongInput.Empty(field = Fields.ADDRESS, input = address))
        } else {
            Either.Right(address)
        }
    }

}