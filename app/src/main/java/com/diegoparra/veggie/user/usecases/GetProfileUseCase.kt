package com.diegoparra.veggie.user.usecases

import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.auth.domain.Profile
import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.user.domain.UserRepository
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {

    suspend fun getProfile(): Either<Failure, Profile> {
        return authRepository.getProfile()
    }

}