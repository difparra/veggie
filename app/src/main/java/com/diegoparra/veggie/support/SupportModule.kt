package com.diegoparra.veggie.support

import com.diegoparra.veggie.support.data.SupportRepositoryImpl
import com.diegoparra.veggie.support.domain.SupportRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SupportModule {

    @Binds
    abstract fun bindsSupportRepository(
        supportRepositoryImpl: SupportRepositoryImpl
    ) : SupportRepository

}