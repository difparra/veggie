package com.diegoparra.veggie.user.data

import com.diegoparra.veggie.user.entities_and_repo.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val userPrefs: UserPrefs
) : UserRepository {

    override suspend fun isSignedIn(): Boolean {
        return userApi.isSignedIn()
    }

    override suspend fun getLastSignedInEmail(): String? {
        return userPrefs.getLastSignedInEmail()
    }


}