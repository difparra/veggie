package com.diegoparra.veggie.user.usecases

import com.diegoparra.veggie.auth.domain.AuthRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke() = authRepository.signOut()
}