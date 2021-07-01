package com.diegoparra.veggie.core.utils_repo

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UtilsRepoModule {

    @Binds
    abstract fun bindsUtilsRepository(
        utilsRepositoryImpl: UtilsRepositoryImpl
    ): UtilsRepository

}