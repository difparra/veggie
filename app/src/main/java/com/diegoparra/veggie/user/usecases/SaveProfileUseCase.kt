package com.diegoparra.veggie.user.usecases

import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.core.*
import com.diegoparra.veggie.user.domain.UserRepository
import javax.inject.Inject

class SaveProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) {

    fun validateName(name: String): Either<SignInFailure.WrongInput, String> {
        return TextInputValidation.forName(name)
    }

    suspend fun saveName(name: String) : Either<Failure, Unit> {
        validateName(name).let {
            if(it is Either.Left) {
                return it
            }
        }
        return updateName(name)
    }

    private suspend fun updateName(name: String): Either<Failure, Unit> {
        return updateAuthRepo(name)
            .suspendFlatMap { updateUserRepo(name) }
    }

    private suspend fun updateAuthRepo(name: String): Either<Failure, Unit> {
        return authRepository.updateProfile(name = name)
    }

    private suspend fun updateUserRepo(name: String): Either<Failure, Unit> {
        return authRepository.getIdCurrentUser().suspendFlatMap {
            userRepository.updateUserData(id = it, name = name)
        }
    }



}