package com.diegoparra.veggie.user

import com.diegoparra.veggie.user.data.UserRepositoryImpl
import com.diegoparra.veggie.user.entities_and_repo.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UserModule {

    @Binds
    abstract fun bindsUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ) : UserRepository

}