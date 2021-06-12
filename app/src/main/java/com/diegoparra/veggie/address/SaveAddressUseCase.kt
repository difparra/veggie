package com.diegoparra.veggie.address

import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.user.domain.UserRepository
import java.util.*
import javax.inject.Inject

class SaveAddressUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(address: String, details: String) : Either<Failure, Unit> {
        //  Validate fields
        validateAddress(address).let {
            if(it is Either.Left) {
                return it
            }
        }
        return saveInDatabase(address, details)
    }

    private fun validateAddress(address: String) : Either<SignInFailure.WrongInput, String> {
        return TextInputValidation.forAddress(address)
    }

    private suspend fun saveInDatabase(address: String, details: String): Either<Failure, Unit> {
        return getIdCurrentUser()
            .suspendFlatMap {
                val addressObj = Address(
                    id = UUID.randomUUID().toString(),
                    address = address,
                    details = details
                )
                userRepository.addAddress(userId = it, address = addressObj)
            }
    }

    private suspend fun getIdCurrentUser(): Either<Failure, String> {
        return authRepository.getIdCurrentUser()
    }

}