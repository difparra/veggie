package com.diegoparra.veggie.user.address.usecases

import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.user.address.domain.Address
import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.suspendFlatMap
import com.diegoparra.veggie.user.address.domain.AddressRepository
import javax.inject.Inject

class GetAddressUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val addressRepository: AddressRepository
) {

    suspend fun getAddressList(): Either<Failure, List<Address>> {
        return getIdCurrentUser().suspendFlatMap {
            addressRepository.getAddressList(it)
        }
    }

    suspend fun getSelectedAddressId(): String? {
        return when (val addressId = addressRepository.getSelectedAddressId()) {
            is Either.Left -> null
            is Either.Right -> addressId.b
        }
    }


    private suspend fun getIdCurrentUser(): Either<Failure, String> {
        return authRepository.getIdCurrentUser()
    }

}