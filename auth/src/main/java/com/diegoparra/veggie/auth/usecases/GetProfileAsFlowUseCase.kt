package com.diegoparra.veggie.auth.usecases

import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.map
import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.auth.domain.Profile
import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.core.kotlin.Failure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetProfileAsFlowUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    //  Do not send profile to viewModel, as it is from other module do not make it visible in ui layer,
    //  it should be only visible in domain layer and only when necessary
    private val profile = authRepository.getProfileAsFlow()

    operator fun invoke(): Flow<Either<Failure, Profile>> {
        return profile
    }

    fun isSignedIn() = profile.map { it is Either.Right }.distinctUntilChanged()
    fun getName() = profile.map { it.map { it.name } }
    fun getEmail() = profile.map { it.map { it.email } }
    fun getPhotoUrl() = profile.map { it.map { it.photoUrl } }

}