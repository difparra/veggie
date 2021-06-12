package com.diegoparra.veggie.user.usecases

import com.diegoparra.veggie.address.Address
import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.suspendFlatMap
import com.diegoparra.veggie.user.domain.UserRepository
import javax.inject.Inject

class GetAddressUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) {

    suspend fun getAddressList(): Either<Failure, List<Address>> {
        return getIdCurrentUser().suspendFlatMap {
            userRepository.getAddressList(it)
        }
    }

    suspend fun getSelectedAddressId(): String? {
        return when(val addressId = userRepository.getSelectedAddressId()) {
            is Either.Left -> null
            is Either.Right -> addressId.b
        }
    }


    private suspend fun getIdCurrentUser(): Either<Failure, String> {
        return authRepository.getIdCurrentUser()
    }

}