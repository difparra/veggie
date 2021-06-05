package com.diegoparra.veggie.auth.usecases

import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.auth.domain.Profile
import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.SignInFailure
import com.diegoparra.veggie.core.suspendFlatMap
import com.diegoparra.veggie.user.domain.UserRepository
import timber.log.Timber

abstract class SignInUseCase<Params> constructor(
    protected val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(params: Params): Either<Failure, Unit> {
        return validate(params)
            .suspendFlatMap { signIn(params) }
            .suspendFlatMap { saveData(it) }
    }

    abstract suspend fun validate(params: Params) : Either<SignInFailure.ValidationFailures, Unit>

    abstract suspend fun signIn(params: Params): Either<Failure, Profile>

    open suspend fun saveData(profile: Profile) : Either<Failure, Unit> {
        Timber.d("saveData called with profile = $profile")
        return userRepository.updateUserData(
            id = profile.id,
            email = profile.email,
            name = profile.name
        )
    }

}