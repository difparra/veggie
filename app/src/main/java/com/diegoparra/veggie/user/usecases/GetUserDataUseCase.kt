package com.diegoparra.veggie.user.usecases

import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.auth.domain.Profile
import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.suspendFlatMap
import com.diegoparra.veggie.user.domain.Address
import com.diegoparra.veggie.user.domain.User
import com.diegoparra.veggie.user.domain.UserRepository
import javax.inject.Inject

class GetUserDataUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {

    //  Do not send profile to viewModel, as it is from other module do not make it visible in ui layer,
    //  it should be only visible in domain layer and only when necessary
    suspend operator fun invoke(): Either<Failure, User> {
        return getIdCurrentUser().suspendFlatMap {
            userRepository.getUser(it)
        }
    }

    suspend fun getProfile(): Either<Failure, Profile> {
        return authRepository.getProfile()
    }

    suspend fun getAddressList(): Either<Failure, List<Address>> {
        return getIdCurrentUser().suspendFlatMap {
            userRepository.getAddressListForUser(it)
        }
    }


    private suspend fun getIdCurrentUser(): Either<Failure, String> {
        return authRepository.getIdCurrentUser()
    }

}