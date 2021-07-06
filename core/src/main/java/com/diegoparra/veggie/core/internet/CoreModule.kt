package com.diegoparra.veggie.core.internet

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreModule {

    @Binds
    abstract fun bindsCoreRepository(
        coreRepositoryImpl: CoreRepositoryImpl
    ): CoreRepository

}