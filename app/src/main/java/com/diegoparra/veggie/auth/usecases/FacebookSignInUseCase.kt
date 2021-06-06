package com.diegoparra.veggie.auth.usecases

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.auth.domain.AuthResults
import com.diegoparra.veggie.auth.domain.Profile
import com.diegoparra.veggie.core.SignInFailure
import com.diegoparra.veggie.user.domain.UserRepository
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import timber.log.Timber
import javax.inject.Inject

class FacebookSignInUseCase @Inject constructor(
    authRepository: AuthRepository,
    userRepository: UserRepository
): SignInUseCase<Either<FacebookException, LoginResult>>(authRepository, userRepository) {

    override suspend fun validate(params: Either<FacebookException, LoginResult>): Either<SignInFailure.ValidationFailures, Unit> {
        //  There are no previous validations to perform when signing in with Facebook
        return Either.Right(Unit)
    }

    override suspend fun signIn(params: Either<FacebookException, LoginResult>): Either<Failure, AuthResults> {
        Timber.d("signIn called with params = $params")
        return when(params){
            is Either.Left -> Either.Left(Failure.ServerError(params.a))
            is Either.Right -> authRepository.signInWithFacebookResult(params.b)
        }
    }

}