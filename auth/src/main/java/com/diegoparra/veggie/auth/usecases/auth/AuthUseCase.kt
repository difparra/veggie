package com.diegoparra.veggie.auth.usecases.auth

import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.auth.domain.AuthResults
import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.suspendFlatMap
import com.diegoparra.veggie.auth.utils.AuthCallbacks
import com.diegoparra.veggie.auth.utils.AuthFailure

abstract class AuthUseCase<Params> constructor(
    protected val authRepository: AuthRepository,
    private val authCallbacks: AuthCallbacks
) {

    suspend operator fun invoke(params: Params): Either<Failure, Unit> {
        return validate(params)
            .suspendFlatMap { authenticate(params) }
            .suspendFlatMap { triggerCallbacks(it) }
    }

    abstract suspend fun validate(params: Params): Either<AuthFailure.ValidationFailures, Unit>

    abstract suspend fun authenticate(params: Params): Either<AuthFailure, AuthResults>

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