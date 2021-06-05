package com.diegoparra.veggie.user.usecases

import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.core.map
import com.diegoparra.veggie.user.domain.UserRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetUserDataUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {

    //  Do not send profile to viewModel, as it is from other module do not make it visible in ui layer,
    //  it should be only visible in domain layer and only when necessary
    private val profile = authRepository.getProfile()
    fun getEmail() = profile.map { it.map { it.email } }
    fun getName() = profile.map { it.map { it.name } }

    suspend fun getPhoneNumber() = userRepository.getPhoneNumber()
    suspend fun getAddress() = userRepository.getAddress()

}