package com.diegoparra.veggie.phone_number_verification

import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.TextInputValidation
import com.diegoparra.veggie.core.suspendFlatMap
import com.diegoparra.veggie.user.domain.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import javax.inject.Inject

class SavePhoneNumberUseCase @Inject constructor(
    val auth: FirebaseAuth,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {

    suspend fun verifyPhoneNumberWithPhoneAuthCredential(phoneNumber: String, phoneAuthCredential: PhoneAuthCredential): Either<Failure, Unit> {
        return updateAuthRepo(credential = phoneAuthCredential)
            .suspendFlatMap {
                updateUserRepo(phoneNumber = phoneNumber)
            }
    }

    fun validatePhoneNumber(phoneNumber: String): Either<Failure, String> {
        return TextInputValidation.forPhoneNumber(phoneNumber.trim())
    }

    private suspend fun updateAuthRepo(credential: PhoneAuthCredential): Either<Failure, Unit>{
        return authRepository.updatePhoneNumber(credential)
    }

    private suspend fun updateUserRepo(phoneNumber: String): Either<Failure, Unit>{
        return authRepository.getIdCurrentUser().suspendFlatMap {
            userRepository.updateUserData(userId = it, phoneNumber = phoneNumber)
        }
    }

}