package com.diegoparra.veggie.products

import android.content.Context
import androidx.room.Room
import com.diegoparra.veggie.products.data.cart.CartDao
import com.diegoparra.veggie.products.data.cart.CartRepositoryImpl
import com.diegoparra.veggie.products.data.VeggieDatabase
import com.diegoparra.veggie.products.domain.repositories.CartRepository
import com.diegoparra.veggie.products.domain.repositories.ProductsRepository
import com.diegoparra.veggie.products.fakedata.FakeProductsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProductsModule {

    @Provides
    fun providesProductsRepository() : ProductsRepository {
        return FakeProductsRepository()
    }

    /*@Provides
    fun providesCartRepository() : CartRepository {
        return FakeCartRepository()
    }*/

    @Singleton
    @Provides
    fun providesVeggieDatabase(@ApplicationContext appContext: Context) : VeggieDatabase {
        return Room
                .databaseBuilder(appContext, VeggieDatabase::class.java, VeggieDatabase.DB_NAME)
                .build()
    }

    @Provides
    fun providesCartDao(veggieDatabase: VeggieDatabase) : CartDao {
        return veggieDatabase.cartDao()
    }

    @Provides
    fun providesCartRepository(cartDao: CartDao) : CartRepository {
        return CartRepositoryImpl(cartDao)
    }
}