package com.diegoparra.veggie.auth.usecases

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.auth.domain.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import javax.inject.Inject

class GoogleSignInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(account: Either<ApiException, GoogleSignInAccount>): Either<Failure, Unit> {
        return when(account){
            is Either.Left -> Either.Left(Failure.ServerError(account.a))
            is Either.Right -> authRepository.signInWithGoogleAccount(account.b)
        }
    }
}