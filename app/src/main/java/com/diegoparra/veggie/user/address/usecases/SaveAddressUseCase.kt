package com.diegoparra.veggie.user.address.usecases

import com.diegoparra.veggie.user.auth.domain.AuthRepository
import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.user.address.domain.Address
import com.diegoparra.veggie.user.address.domain.AddressRepository
import java.util.*
import javax.inject.Inject

class SaveAddressUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val addressRepository: AddressRepository
) {

    suspend operator fun invoke(
        address: String,
        details: String,
        instructions: String
    ): Either<Failure, Unit> {
        //  Validate fields
        validateAddress(address).let {
            if (it is Either.Left) {
                return it
            }
        }
        return saveInDatabase(address, details, instructions)
    }

    private fun validateAddress(address: String): Either<SignInFailure.WrongInput, String> {
        return TextInputValidation.forAddress(address)
    }

    private suspend fun saveInDatabase(
        address: String,
        details: String,
        instructions: String
    ): Either<Failure, Unit> {
        return getIdCurrentUser()
            .suspendFlatMap {
                val addressObj = Address(
                    id = UUID.randomUUID().toString(),
                    address = address,
                    details = details,
                    instructions = instructions
                )
                addressRepository.addAddress(userId = it, address = addressObj)
            }
    }


    private suspend fun getIdCurrentUser(): Either<Failure, String> {
        return authRepository.getIdCurrentUser()
    }

}