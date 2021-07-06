package com.diegoparra.veggie.auth.usecases.auth

import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.auth.domain.AuthResults
import com.diegoparra.veggie.auth.utils.AuthCallbacks
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.input_validation.InputFailure
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import javax.inject.Inject

class GoogleAuthUseCase @Inject constructor(
    authRepository: AuthRepository,
    authCallbacks: AuthCallbacks
) : AuthUseCase<Either<ApiException, GoogleSignInAccount>>(authRepository, authCallbacks) {

    override suspend fun validate(params: Either<ApiException, GoogleSignInAccount>): Either<InputFailure.InputFailuresList, Unit> {
        //  There are no previous validations to perform when signing in with Google
        return Either.Right(Unit)
    }

    override suspend fun authenticate(params: Either<ApiException, GoogleSignInAccount>): Either<Failure, AuthResults> {
        return when (params) {
            is Either.Left -> Either.Left(Failure.ServerError(params.a))
            is Either.Right -> authRepository.signInWithGoogleAccount(params.b)
        }
    }
}