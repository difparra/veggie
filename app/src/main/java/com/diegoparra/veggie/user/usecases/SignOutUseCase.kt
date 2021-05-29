package com.diegoparra.veggie.user.usecases

import com.diegoparra.veggie.user.entities_and_repo.UserRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    operator fun invoke() = userRepository.signOut()
}