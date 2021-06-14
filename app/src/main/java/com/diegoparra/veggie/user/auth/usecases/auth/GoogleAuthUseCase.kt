package com.diegoparra.veggie.user.auth.usecases.auth

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.user.auth.domain.AuthRepository
import com.diegoparra.veggie.user.auth.domain.AuthResults
import com.diegoparra.veggie.core.SignInFailure
import com.diegoparra.veggie.user.auth.domain.AuthCallbacks
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import javax.inject.Inject

class GoogleAuthUseCase @Inject constructor(
    authRepository: AuthRepository,
    authCallbacks: AuthCallbacks
) : AuthUseCase<Either<ApiException, GoogleSignInAccount>>(authRepository, authCallbacks) {

    override suspend fun validate(params: Either<ApiException, GoogleSignInAccount>): Either<SignInFailure.ValidationFailures, Unit> {
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