package com.diegoparra.veggie.user.usecases

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.user.entities_and_repo.UserRepository
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import javax.inject.Inject

class FacebookSignInUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(loginResult: Either<FacebookException, LoginResult>): Either<Failure, Unit> {
        return when(loginResult) {
            is Either.Left -> Either.Left(Failure.ServerError(loginResult.a))
            is Either.Right -> userRepository.signInWithFacebookResult(loginResult.b)
        }
    }

}