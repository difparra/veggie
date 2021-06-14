package com.diegoparra.veggie.user.address.usecases

import com.diegoparra.veggie.user.address.domain.Address
import com.diegoparra.veggie.user.auth.domain.AuthRepository
import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.suspendFlatMap
import com.diegoparra.veggie.user.UserRepository
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