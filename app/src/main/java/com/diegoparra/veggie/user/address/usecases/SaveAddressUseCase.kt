package com.diegoparra.veggie.user.address.usecases

import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.auth.utils.TextInputValidation
import com.diegoparra.veggie.core.kotlin.*
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
            .suspendMap { selectNewAddressAsMain(it.id) }
    }

    private fun validateAddress(address: String): Either<AuthFailure.WrongInput, String> {
        return TextInputValidation.forAddress(address)
    }

    private suspend fun saveInDatabase(
        address: String, details: String, instructions: String
    ): Either<Failure, Address> {
        return authRepository.getIdCurrentUser()
            .suspendFlatMap {
                val addressObj = Address(
                    id = generateUniqueId(),
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