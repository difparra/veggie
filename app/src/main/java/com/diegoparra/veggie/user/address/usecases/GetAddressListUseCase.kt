package com.diegoparra.veggie.user.address.usecases

import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.flatMap
import com.diegoparra.veggie.user.address.domain.Address
import com.diegoparra.veggie.user.address.domain.AddressRepository
import javax.inject.Inject

class GetAddressListUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val addressRepository: AddressRepository
) {

    suspend operator fun invoke(): Either<Failure, List<Address>> {
        return authRepository.getIdCurrentUser().flatMap {
            addressRepository.getAddressList(userId = it)
        }
    }

}