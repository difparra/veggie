package com.diegoparra.veggie.core.kotlin.input_validation

import com.diegoparra.veggie.core.kotlin.Either
import timber.log.Timber
import com.diegoparra.veggie.core.kotlin.input_validation.InputFailure.Companion.Field
import com.diegoparra.veggie.core.kotlin.validateEmail

object TextInputValidation {

    fun validateNotEmpty(str: String, field: Field): Either<InputFailure, String> {
        return if(str.isEmpty()) {
            Either.Left(InputFailure.Empty(field = field, input = str))
        } else {
            Either.Right(str)
        }
    }

    fun forEmail(email: String): Either<InputFailure, String> {
        return if (email.isEmpty()) {
            Either.Left(InputFailure.Empty(field = Field.EMAIL, input = email))
        } else if (!validateEmail(email)) {
            Either.Left(InputFailure.Invalid(field = Field.EMAIL, input = email))
        } else {
            Either.Right(email)
        }
    }

    fun forPassword(password: String): Either<InputFailure, String> {
        return if (password.isEmpty()) {
            Either.Left(InputFailure.Empty(field = Field.PASSWORD, input = password))
        } else if (password.length < 6) {
            Either.Left(
                InputFailure.Short(
                    field = Field.PASSWORD,
                    input = password,
                    minLength = 6
                )
            )
        } else {
            Either.Right(password)
        }
    }

    fun forName(name: String): Either<InputFailure, String> {
        return if (name.isEmpty()) {
            Either.Left(InputFailure.Empty(field = Field.NAME, input = name))
        } else {
            Either.Right(name)
        }
    }

    fun forPhoneNumber(phoneNumber: String): Either<InputFailure, String> {
        Timber.d("phoneNumber = $phoneNumber, phoneNumberLength = ${phoneNumber.length}")
        return if (phoneNumber.isEmpty()) {
            Either.Left(InputFailure.Empty(field = Field.PHONE_NUMBER, input = phoneNumber))
        } else if (
            !(phoneNumber.startsWith("+57 3") || phoneNumber.startsWith("+573"))
            || phoneNumber.length != 13
        ) {
            Either.Left(InputFailure.Invalid(field = Field.PHONE_NUMBER, input = phoneNumber))
        } else {
            Either.Right(phoneNumber)
        }
    }

    fun forAddress(address: String): Either<InputFailure, String> {
        return if(address.isBlank()) {
            Either.Left(InputFailure.Empty(field = Field.ADDRESS, input = address))
        } else {
            Either.Right(address)
        }
    }

}