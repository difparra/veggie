package com.diegoparra.veggie.address

import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.user.domain.UserRepository
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
                //  Do not need to add logic for id in here, that will correspond to DtosTransformations, that
                //  will generate the corresponding id to save in database.
                val addressObj = Address(
                    id = "",
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