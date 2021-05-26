package com.diegoparra.veggie.user.usecases

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.SignInFailure
import com.diegoparra.veggie.core.validateEmail

object TextInputValidation {

    fun forEmail(email: String): Either<SignInFailure.WrongInput, String> {
        return if (email.isEmpty()) {
            Either.Left(SignInFailure.WrongInput.Email.Empty)
        } else if (!validateEmail(email)) {
            Either.Left(SignInFailure.WrongInput.Email.Invalid)
        } else {
            Either.Right(email)
        }
    }

    fun forPassword(password: String): Either<SignInFailure.WrongInput, String> {
        return if (password.isEmpty()) {
            Either.Left(SignInFailure.WrongInput.Password.Empty)
        } else if (password.length < SignInFailure.WrongInput.Password.Short.minLength) {
            Either.Left(SignInFailure.WrongInput.Password.Short)
        } else {
            Either.Right(password)
        }
    }

    fun forName(name: String): Either<SignInFailure.WrongInput, String> {
        return if (name.isEmpty()) {
            Either.Left(SignInFailure.WrongInput.NameEmpty)
        } else {
            Either.Right(name)
        }
    }

}