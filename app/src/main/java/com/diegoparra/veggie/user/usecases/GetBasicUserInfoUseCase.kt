package com.diegoparra.veggie.user.usecases

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.map
import com.diegoparra.veggie.user.entities_and_repo.BasicUserInfo
import com.diegoparra.veggie.user.entities_and_repo.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetBasicUserInfoUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    private val basicUserInfo = userRepository.getBasicUserInfo()

    operator fun invoke(): Flow<Either<Failure, BasicUserInfo>> = basicUserInfo

    fun isSignedIn() = basicUserInfo.map { it is Either.Right }.distinctUntilChanged()
    fun getName() = basicUserInfo.map { it.map { it.name } }
    fun getEmail() = basicUserInfo.map { it.map { it.email } }
    fun getPhotoUrl() = basicUserInfo.map { it.map { it.photoUrl } }

}