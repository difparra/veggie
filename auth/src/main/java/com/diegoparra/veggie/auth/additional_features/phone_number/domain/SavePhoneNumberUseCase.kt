package com.diegoparra.veggie.auth.additional_features.phone_number.domain

import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.auth.utils.AuthCallbacks
import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.flatMap
import com.diegoparra.veggie.core.kotlin.input_validation.InputFailure
import com.diegoparra.veggie.core.kotlin.input_validation.TextInputValidation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import javax.inject.Inject

class SavePhoneNumberUseCase @Inject constructor(
    val auth: FirebaseAuth,
    private val authRepository: AuthRepository,
    private val authCallbacks: AuthCallbacks
) {

    fun validatePhoneNumber(phoneNumber: String): Either<InputFailure, String> {
        return TextInputValidation.forPhoneNumber(phoneNumber.trim())
    }

    suspend fun verifyPhoneNumberWithPhoneAuthCredential(
        phoneNumber: String,
        phoneAuthCredential: PhoneAuthCredential
    ): Either<Failure, Unit> {
        return updateAuthRepo(credential = phoneAuthCredential)
            .flatMap { onPhoneVerified(phoneNumber) }
    }

    private suspend fun updateAuthRepo(credential: PhoneAuthCredential): Either<Failure, Unit> {
        return authRepository.updatePhoneNumber(credential)
    }

    private suspend fun onPhoneVerified(phoneNumber: String): Either<Failure, Unit> {
        return authRepository.getIdCurrentUser().flatMap {
            authCallbacks.onPhoneVerified(userId = it, phoneNumber = phoneNumber)
        }
    }

}