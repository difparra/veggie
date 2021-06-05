package com.diegoparra.veggie.auth.usecases

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.auth.domain.Profile
import com.diegoparra.veggie.core.SignInFailure
import com.diegoparra.veggie.user.domain.UserRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import javax.inject.Inject

class GoogleSignInUseCase @Inject constructor(
    authRepository: AuthRepository,
    userRepository: UserRepository
): SignInUseCase<Either<ApiException, GoogleSignInAccount>>(authRepository, userRepository) {

    override suspend fun validate(params: Either<ApiException, GoogleSignInAccount>): Either<SignInFailure.ValidationFailures, Unit> {
        //  There are no previous validations to perform when signing in with Google
        return Either.Right(Unit)
    }

    override suspend fun signIn(params: Either<ApiException, GoogleSignInAccount>): Either<Failure, Profile> {
        return when(params){
            is Either.Left -> Either.Left(Failure.ServerError(params.a))
            is Either.Right -> authRepository.signInWithGoogleAccount(params.b)
        }
    }
}