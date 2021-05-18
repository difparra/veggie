package com.diegoparra.veggie.products.cart

import com.diegoparra.veggie.products.cart.data.CartRepositoryImpl
import com.diegoparra.veggie.products.cart.domain.CartRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CartModule {

    @Binds
    abstract fun bindsCartRepository(
        cartRepositoryImpl: CartRepositoryImpl
    ) : CartRepository


    //  Also necessary cartDao, but inserted with the veggieDatabase in the appModule

}