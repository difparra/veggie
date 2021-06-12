package com.diegoparra.veggie.address

import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.suspendFlatMap
import com.diegoparra.veggie.user.domain.UserRepository
import javax.inject.Inject

class DeleteAddressUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(address: Address): Either<Failure, Unit> {
        return authRepository.getIdCurrentUser().suspendFlatMap {
            userRepository.deleteAddress(it, address)
        }
    }

}