package com.diegoparra.veggie.user.data

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.map
import com.diegoparra.veggie.user.data.UserTransformations.toSignInMethodList
import com.diegoparra.veggie.user.entities_and_repo.SignInMethod
import com.diegoparra.veggie.user.entities_and_repo.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val userPrefs: UserPrefs
) : UserRepository {


    override fun isSignedIn(): Flow<Boolean> {
        return userApi.isSignedInAsFlow()
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getSignInMethodsForEmail(email: String): Either<Failure, List<SignInMethod>> =
        withContext(Dispatchers.IO) {
            userApi.getSignInMethodsForEmail(email)
                .map { it.toSignInMethodList() }
        }


    /*override suspend fun isSignedIn(): Boolean {
        return userApi.isSignedIn()
    }

    override suspend fun getLastSignedInEmail(): String? {
        return userPrefs.getLastSignedInEmail()
    }*/

}