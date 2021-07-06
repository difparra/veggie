package com.diegoparra.veggie.order.domain

import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.Source
import com.diegoparra.veggie.products.domain.ProductsRepository
import java.util.concurrent.TimeUnit

interface OrderRepository {

    //  Config values:  Will always return a value, even if calling them produced failure.
    //  In such case, they will return a default value.
    suspend fun getMinOrder(): Either<Failure, Int>
    suspend fun getDeliveryBaseCosts(): Either<Failure, DeliveryBaseCosts>
    suspend fun getDeliveryScheduleConfig(): Either<Failure, DeliveryScheduleConfig>

    suspend fun sendOrder(order: Order): Either<Failure, String>

    suspend fun getOrdersForUser(userId: String, source: Source = Source.DEFAULT(0)): Either<Failure, List<Order>>

}