package com.diegoparra.veggie.order.domain

import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure

interface OrderRepository {

    //  Config values:  Will always return a value, even if calling them produced failure.
    //  In such case, they will return a default value.
    suspend fun getMinOrder(): Either<Failure, Int>
    suspend fun getDeliveryBaseCosts(): Either<Failure, DeliveryBaseCosts>
    suspend fun getDeliveryScheduleConfig(): Either<Failure, DeliveryScheduleConfig>

    suspend fun sendOrder(order: Order): Either<Failure, String>
    suspend fun getOrdersForUser(userId: String): Either<Failure, List<Order>>

}