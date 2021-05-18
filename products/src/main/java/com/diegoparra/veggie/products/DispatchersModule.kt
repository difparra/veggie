package com.diegoparra.veggie.products

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {

    @IoDispatcher
    @Provides
    fun providesIoDispatcher() = Dispatchers.IO

}



@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IoDispatcher