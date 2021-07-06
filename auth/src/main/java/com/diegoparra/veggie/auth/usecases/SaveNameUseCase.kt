package com.diegoparra.veggie.auth.usecases

import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.auth.utils.AuthCallbacks
import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.flatMap
import com.diegoparra.veggie.core.kotlin.input_validation.TextInputValidation
import com.diegoparra.veggie.core.kotlin.input_validation.InputFailure
import com.diegoparra.veggie.core.kotlin.onFailure
import javax.inject.Inject

class SaveNameUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val authCallbacks: AuthCallbacks
) {

    fun validateName(name: String): Either<InputFailure, String> {
        return TextInputValidation.forName(name)
    }


    suspend fun saveName(name: String): Either<Failure, Unit> {
        //  Validate fields
        validateName(name).onFailure {
            return Either.Left(it)
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