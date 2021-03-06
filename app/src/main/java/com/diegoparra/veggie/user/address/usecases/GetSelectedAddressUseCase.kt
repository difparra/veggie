package com.diegoparra.veggie.user.address.usecases

import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.core.kotlin.*
import com.diegoparra.veggie.user.address.domain.Address
import com.diegoparra.veggie.user.address.domain.AddressRepository
import javax.inject.Inject

class GetSelectedAddressUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val addressRepository: AddressRepository
) {

    suspend operator fun invoke(
        userId: String? = null,
        addressList: List<Address>? = null
    ): Either<Failure, Address?> {
        val mUserId = userId
            ?: authRepository.getIdCurrentUser().getOrElse { return@invoke Either.Left(it) }
        return addressRepository.getSelectedAddress(userId = mUserId, addressList = addressList)
    }

}