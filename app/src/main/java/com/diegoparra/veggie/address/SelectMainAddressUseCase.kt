package com.diegoparra.veggie.address

import com.diegoparra.veggie.user.domain.UserRepository
import javax.inject.Inject

class SelectMainAddressUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(addressId: String) {
        userRepository.setSelectedAddressId(addressId)
    }

}