package com.diegoparra.veggie.user.auth.usecases

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.map
import com.diegoparra.veggie.user.auth.domain.AuthRepository
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetProfileAsFlowUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    //  Do not send profile to viewModel, as it is from other module do not make it visible in ui layer,
    //  it should be only visible in domain layer and only when necessary
    private val profile = authRepository.getProfileAsFlow()

    fun isSignedIn() = profile.map { it is Either.Right }.distinctUntilChanged()
    fun getName() = profile.map { it.map { it.name } }
    fun getEmail() = profile.map { it.map { it.email } }
    fun getPhotoUrl() = profile.map { it.map { it.photoUrl } }

}