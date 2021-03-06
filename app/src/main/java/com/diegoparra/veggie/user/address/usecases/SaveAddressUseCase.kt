package com.diegoparra.veggie.user.address.usecases

import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.core.kotlin.*
import com.diegoparra.veggie.core.kotlin.input_validation.InputFailure
import com.diegoparra.veggie.core.kotlin.input_validation.TextInputValidation
import com.diegoparra.veggie.user.address.domain.Address
import com.diegoparra.veggie.user.address.domain.AddressRepository
import javax.inject.Inject

class SaveAddressUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val addressRepository: AddressRepository,
    private val selectMainAddressUseCase: SelectMainAddressUseCase
) {

    suspend operator fun invoke(
        address: String, details: String, instructions: String
    ): Either<Failure, Unit> {
        //  Validate fields
        validateAddress(address).let {
            if (it is Either.Left) {
                return it
            }
        }
        return saveInDatabase(address, details, instructions)
            .map { selectNewAddressAsMain(it.id) }
    }

    private fun validateAddress(address: String): Either<InputFailure, String> {
        return TextInputValidation.forAddress(address)
    }

    private suspend fun saveInDatabase(
        address: String, details: String, instructions: String
    ): Either<Failure, Address> {
        return authRepository.getIdCurrentUser()
            .flatMap {
                val addressObj = Address(
                    id = getUniqueId(),
                    address = address,
                    details = details,
                    instructions = instructions
                )
                addressRepository.addAddress(userId = it, address = addressObj)
            }
    }

    private suspend fun selectNewAddressAsMain(addressId: String) {
        selectMainAddressUseCase(addressId)
    }


}