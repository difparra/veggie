package com.diegoparra.veggie.user.usecases.utils

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.SignInFailure
import com.diegoparra.veggie.core.validateEmail
import com.diegoparra.veggie.user.entities_and_repo.UserConstants

object TextInputValidation {

    fun forEmail(email: String): Either<SignInFailure.WrongInput, String> {
        val emailField = UserConstants.SignInFields.EMAIL
        return if (email.isEmpty()) {
            Either.Left(SignInFailure.WrongInput.Empty(field = emailField, input = email))
        } else if (!validateEmail(email)) {
            Either.Left(SignInFailure.WrongInput.Invalid(field = emailField, input = email))
        } else {
            Either.Right(email)
        }
    }

    fun forPassword(password: String): Either<SignInFailure.WrongInput, String> {
        val passwordField = UserConstants.SignInFields.PASSWORD
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
            Either.Left(SignInFailure.WrongInput.Empty(field = UserConstants.SignInFields.NAME, input = name))
        } else {
            Either.Right(name)
        }
    }

}