package com.diegoparra.veggie.auth.usecases.auth

import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.auth.domain.AuthResults
import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.auth.utils.AuthCallbacks
import com.diegoparra.veggie.core.kotlin.flatMap
import com.diegoparra.veggie.core.kotlin.input_validation.InputFailure

abstract class AuthUseCase<Params> constructor(
    protected val authRepository: AuthRepository,
    private val authCallbacks: AuthCallbacks
) {

    suspend operator fun invoke(params: Params): Either<Failure, Unit> {
        return validate(params)
            .flatMap { additionalValidations(params) }
            .flatMap { authenticate(params) }
            .flatMap { triggerCallbacks(it) }
    }

    abstract suspend fun validate(params: Params): Either<InputFailure.InputFailuresList, Unit>

    //  In order to check some additional validation, such as if the account is not linked to the signInMethod
    open suspend fun additionalValidations(params: Params): Either<Failure, Unit> {
        return Either.Right(Unit)
    }

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