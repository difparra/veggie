package com.diegoparra.veggie.user.auth.usecases

import com.diegoparra.veggie.user.auth.domain.AuthRepository
import com.diegoparra.veggie.user.auth.domain.Profile
import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.user.UserRepository
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend fun getProfile(): Either<Failure, Profile> {
        return authRepository.getProfile()
    }

}