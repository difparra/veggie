package com.diegoparra.veggie.auth.usecases.auth

import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.auth.domain.AuthResults
import com.diegoparra.veggie.auth.utils.AuthCallbacks
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.input_validation.InputFailure
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import timber.log.Timber
import javax.inject.Inject

class FacebookAuthUseCase @Inject constructor(
    authRepository: AuthRepository,
    authCallbacks: AuthCallbacks
) : AuthUseCase<Either<FacebookException, LoginResult>>(authRepository, authCallbacks) {

    override suspend fun validate(params: Either<FacebookException, LoginResult>): Either<InputFailure.InputFailuresList, Unit> {
        //  There are no previous validations to perform when signing in with Facebook
        return Either.Right(Unit)
    }

    override suspend fun authenticate(params: Either<FacebookException, LoginResult>): Either<Failure, AuthResults> {
        Timber.d("signIn called with params = $params")
        return when (params) {
            is Either.Left -> Either.Left(Failure.ServerError(params.a))
            is Either.Right -> authRepository.signInWithFacebookResult(params.b)
        }
    }

}