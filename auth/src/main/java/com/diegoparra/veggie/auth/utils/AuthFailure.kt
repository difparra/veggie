package com.diegoparra.veggie.auth.utils

import com.diegoparra.veggie.auth.R
import com.diegoparra.veggie.core.kotlin.Failure

sealed class AuthFailure : Failure.FeatureFailure() {


    sealed class SignInState(override val strRes: Int? = null) : AuthFailure() {

        object NotSignedIn :
            SignInState(strRes = R.string.failure_not_signed_in)

        object Anonymous :
            SignInState(strRes = R.string.failure_not_signed_in)
    }


    sealed class WrongSignInMethod(
        val email: String,
        override val strRes: Int? = null, override val formatArgs: Array<out Any> = emptyArray()
    ) : AuthFailure() {

        class NewUser(email: String) :
            WrongSignInMethod(email, strRes = R.string.failure_new_user)

        class ExistentUser(email: String) :
            WrongSignInMethod(email, strRes = R.string.failure_existent_user)

        class SignInMethodNotLinked(
            email: String,
            val signInMethod: String,
            val linkedSignInMethods: List<String>,
        ) : WrongSignInMethod(
            email,
            strRes = R.string.failure_not_linked_sign_in_method,
            formatArgs = arrayOf(linkedSignInMethods.joinToString(","))
        )

        class Unknown(email: String, override val debugMessage: String) :
            WrongSignInMethod(email)
    }


    sealed class PhoneAuthFailures(
        override val strRes: Int? = null
    ) : AuthFailure() {
        object InvalidRequest : PhoneAuthFailures(R.string.failure_invalid_request_phone_number)
        object TooManyRequests : PhoneAuthFailures(R.string.failure_too_many_requests)
        object InvalidSmsCode : PhoneAuthFailures(R.string.failure_invalid_sms_code)
        object ExpiredSmsCode : PhoneAuthFailures(R.string.failure_code_has_expired)
    }
}