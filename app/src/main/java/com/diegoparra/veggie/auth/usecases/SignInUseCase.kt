package com.diegoparra.veggie.auth.usecases

import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.auth.domain.AuthResults
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
            .suspendFlatMap { saveData(profile = it.profile, isNewUser = it.isNewUser) }
    }

    abstract suspend fun validate(params: Params): Either<SignInFailure.ValidationFailures, Unit>

    abstract suspend fun signIn(params: Params): Either<Failure, AuthResults>

    open suspend fun saveData(profile: Profile, isNewUser: Boolean): Either<Failure, Unit> {
        return if (isNewUser) {
            Timber.d("isNewUser = $isNewUser, calling to update data with profile = $profile")
            userRepository.updateUserData(
                userId = profile.id,
                email = profile.email,
                name = profile.name
            )
        } else {
            Timber.d("isNewUser = $isNewUser. User is not updated because it is not new and should already exist in database. Profile = $profile")
            Either.Right(Unit)
        }
    }

}