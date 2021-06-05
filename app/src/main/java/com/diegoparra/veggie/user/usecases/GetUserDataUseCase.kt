package com.diegoparra.veggie.user.usecases

import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.auth.domain.Profile
import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.map
import com.diegoparra.veggie.core.suspendFlatMap
import com.diegoparra.veggie.user.domain.User
import com.diegoparra.veggie.user.domain.UserRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetUserDataUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {

    //  Do not send profile to viewModel, as it is from other module do not make it visible in ui layer,
    //  it should be only visible in domain layer and only when necessary
    suspend operator fun invoke(): Either<Failure, User> {
        return authRepository.getIdCurrentUser()
            .suspendFlatMap {
                userRepository.getUser(it)
            }
    }

}