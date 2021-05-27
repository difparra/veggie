package com.diegoparra.veggie.user.usecases

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.SignInFailure
import com.diegoparra.veggie.core.validateEmail
import com.diegoparra.veggie.user.entities_and_repo.UserConstants

object TextInputValidation {

    fun forEmail(email: String): Either<SignInFailure.WrongInput, String> {
        val emailField = UserConstants.SignInFields.EMAIL
        return if (email.isEmpty()) {
            Either.Left(SignInFailure.WrongInput.Empty(emailField))
        } else if (!validateEmail(email)) {
            Either.Left(SignInFailure.WrongInput.Invalid(emailField))
        } else {
            Either.Right(email)
        }
    }

    fun forPassword(password: String): Either<SignInFailure.WrongInput, String> {
        val passwordField = UserConstants.SignInFields.PASSWORD
        return if (password.isEmpty()) {
            Either.Left(SignInFailure.WrongInput.Empty(passwordField))
        } else if (password.length < 6) {
            Either.Left(SignInFailure.WrongInput.Short(passwordField, 6))
        } else {
            Either.Right(password)
        }
    }

    fun forName(name: String): Either<SignInFailure.WrongInput, String> {
        return if (name.isEmpty()) {
            Either.Left(SignInFailure.WrongInput.Empty(UserConstants.SignInFields.NAME))
        } else {
            Either.Right(name)
        }
    }

}