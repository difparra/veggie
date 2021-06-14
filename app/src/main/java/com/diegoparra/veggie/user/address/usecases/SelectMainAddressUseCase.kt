package com.diegoparra.veggie.user.address.usecases

import com.diegoparra.veggie.user.address.domain.AddressRepository
import javax.inject.Inject

class SelectMainAddressUseCase @Inject constructor(
    private val addressRepository: AddressRepository
) {

    suspend operator fun invoke(addressId: String) {
        addressRepository.setSelectedAddressId(addressId)
    }

}