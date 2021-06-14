package com.diegoparra.veggie.user.address.usecases

import com.diegoparra.veggie.user.auth.domain.AuthRepository
import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.suspendFlatMap
import com.diegoparra.veggie.user.address.domain.Address
import com.diegoparra.veggie.user.address.domain.AddressRepository
import javax.inject.Inject

class DeleteAddressUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val addressRepository: AddressRepository
) {

    suspend operator fun invoke(address: Address): Either<Failure, Unit> {
        return authRepository.getIdCurrentUser().suspendFlatMap {
            addressRepository.deleteAddress(it, address)
        }
    }

}