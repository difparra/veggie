package com.diegoparra.veggie.user.address.usecases

import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.core.kotlin.getOrElse
import com.diegoparra.veggie.core.kotlin.getOrNull
import com.diegoparra.veggie.user.address.domain.AddressRepository
import javax.inject.Inject

class SelectMainAddressUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val addressRepository: AddressRepository
) {

    suspend operator fun invoke(addressId: String) {
        val currentUserId = authRepository.getIdCurrentUser().getOrElse { "" }
        addressRepository.setSelectedAddress(userId = currentUserId, addressId = addressId)
    }

}