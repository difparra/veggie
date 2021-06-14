package com.diegoparra.veggie.user.edit_profile.auth_phone_number.domain

import com.diegoparra.veggie.user.auth.domain.AuthRepository
import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.TextInputValidation
import com.diegoparra.veggie.core.suspendFlatMap
import com.diegoparra.veggie.user.auth.domain.AuthCallbacks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import javax.inject.Inject

class SavePhoneNumberUseCase @Inject constructor(
    val auth: FirebaseAuth,
    private val authRepository: AuthRepository,
    private val authCallbacks: AuthCallbacks
) {

    fun validatePhoneNumber(phoneNumber: String): Either<Failure, String> {
        return TextInputValidation.forPhoneNumber(phoneNumber.trim())
    }

    suspend fun verifyPhoneNumberWithPhoneAuthCredential(
        phoneNumber: String,
        phoneAuthCredential: PhoneAuthCredential
    ): Either<Failure, Unit> {
        return updateAuthRepo(credential = phoneAuthCredential)
            .suspendFlatMap {
                authRepository.getIdCurrentUser().suspendFlatMap {
                    authCallbacks.onPhoneVerified(userId = it, phoneNumber = phoneNumber)
                }
            }
    }

    private suspend fun updateAuthRepo(credential: PhoneAuthCredential): Either<Failure, Unit> {
        return authRepository.updatePhoneNumber(credential)
    }

}