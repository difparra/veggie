package com.diegoparra.veggie.products

import com.diegoparra.veggie.products.domain.repositories.CartRepository
import com.diegoparra.veggie.products.domain.repositories.ProductsRepository
import com.diegoparra.veggie.products.fakedata.FakeCartRepository
import com.diegoparra.veggie.products.fakedata.FakeProductsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ProductsModule {

    @Provides
    fun providesProductsRepository() : ProductsRepository {
        return FakeProductsRepository()
    }

    @Provides
    fun providesCartRepository() : CartRepository {
        return FakeCartRepository()
    }

}