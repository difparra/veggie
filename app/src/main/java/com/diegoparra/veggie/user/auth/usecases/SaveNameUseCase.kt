package com.diegoparra.veggie.user.auth.usecases

import com.diegoparra.veggie.user.auth.domain.AuthRepository
import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.user.auth.domain.AuthCallbacks
import javax.inject.Inject

class SaveNameUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val authCallbacks: AuthCallbacks
) {

    fun validateName(name: String): Either<SignInFailure.WrongInput, String> {
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
            .suspendFlatMap {
                authRepository.getIdCurrentUser()
                    .suspendFlatMap {
                        authCallbacks.onUpdateProfile(
                            userId = it,
                            name = name
                        )
                    }
            }
    }

}