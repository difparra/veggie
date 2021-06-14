package com.diegoparra.veggie.user.auth.usecases.auth

import com.diegoparra.veggie.user.auth.domain.AuthRepository
import com.diegoparra.veggie.user.auth.domain.AuthResults
import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.SignInFailure
import com.diegoparra.veggie.core.suspendFlatMap
import com.diegoparra.veggie.user.auth.domain.AuthCallbacks

abstract class AuthUseCase<Params> constructor(
    protected val authRepository: AuthRepository,
    private val authCallbacks: AuthCallbacks
) {

    suspend operator fun invoke(params: Params): Either<Failure, Unit> {
        return validate(params)
            .suspendFlatMap { authenticate(params) }
            .suspendFlatMap { triggerCallbacks(it) }
    }

    abstract suspend fun validate(params: Params): Either<SignInFailure.ValidationFailures, Unit>

    abstract suspend fun authenticate(params: Params): Either<Failure, AuthResults>

    open suspend fun triggerCallbacks(authResults: AuthResults): Either<Failure, Unit> {
        return if (authResults.isNewUser) {
            authCallbacks.onUserSignedIn(
                profile = authResults.profile,
                signInMethod = authResults.signInMethod
            )
        } else {
            authCallbacks.onUserSignedUp(
                profile = authResults.profile,
                signInMethod = authResults.signInMethod
            )
        }
    }

}