package com.diegoparra.veggie.auth.usecases

import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.auth.utils.AuthCallbacks
import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.suspendFlatMap
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val authCallbacks: AuthCallbacks
) {

    suspend operator fun invoke() {
        val currentId = authRepository.getIdCurrentUser()
        if(currentId is Either.Right) {
            triggerCallbacks(currentId.b)
        }
        authRepository.signOut()
    }

    private suspend fun triggerCallbacks(userId: String) {
        authCallbacks.onSignOut(userId)
    }

}