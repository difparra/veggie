package com.diegoparra.veggie.auth.usecases

import com.diegoparra.veggie.auth.domain.AuthRepository
import javax.inject.Inject

class IsSignedInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    operator fun invoke() = authRepository.isSignedIn()

}