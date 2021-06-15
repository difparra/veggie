package com.diegoparra.veggie.auth.usecases

import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.auth.domain.Profile
import com.diegoparra.veggie.core.kotlin.Either
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend fun getProfile(): Either<AuthFailure, Profile> {
        return authRepository.getProfile()
    }

}