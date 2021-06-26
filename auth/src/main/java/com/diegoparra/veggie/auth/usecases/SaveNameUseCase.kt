package com.diegoparra.veggie.auth.usecases

import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.auth.utils.AuthCallbacks
import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.auth.utils.TextInputValidation
import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.flatMap
import javax.inject.Inject

class SaveNameUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val authCallbacks: AuthCallbacks
) {

    fun validateName(name: String): Either<AuthFailure.WrongInput, String> {
        return TextInputValidation.forName(name)
    }


    suspend fun saveName(name: String): Either<Failure, Unit> {
        //  Validate fields
        validateName(name).let {
            if (it is Either.Left) {
                return it
            }
        }

        //  Update in authRepo and userRepo
        return authRepository.updateProfile(name = name)
            .flatMap { triggerCallbacks(name = name) }
    }

    private suspend fun triggerCallbacks(name: String): Either<Failure, Unit> {
        return authRepository.getIdCurrentUser()
            .flatMap {
                authCallbacks.onUpdateProfile(
                    userId = it,
                    name = name
                )
            }
    }

}