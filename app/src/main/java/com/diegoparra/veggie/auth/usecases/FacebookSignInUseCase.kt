package com.diegoparra.veggie.auth.usecases

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.core.map
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import javax.inject.Inject

class FacebookSignInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(loginResult: Either<FacebookException, LoginResult>): Either<Failure, Unit> {
        return when(loginResult) {
            is Either.Left -> Either.Left(Failure.ServerError(loginResult.a))
            is Either.Right ->
                authRepository
                    .signInWithFacebookResult(loginResult.b)
                    .map { Unit }
        }
    }

}