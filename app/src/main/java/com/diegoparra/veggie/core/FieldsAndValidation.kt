package com.diegoparra.veggie.core

object Fields {
    const val EMAIL = "email"
    const val PASSWORD = "password"
    const val NAME = "name"
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
            Either.Left(SignInFailure.WrongInput.Short(field = passwordField, input = password, minLength = 6))
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

}