package com.diegoparra.veggie.order

import com.diegoparra.veggie.order.data.OrderRepositoryImpl
import com.diegoparra.veggie.order.domain.OrderRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class OrderModule {

    @Binds
    abstract fun bindsOrderRepository(
        orderRepositoryImpl: OrderRepositoryImpl
    ): OrderRepository


    //      Requires also orderDao, but was inserted in the same appModule with the veggieDatabase

}