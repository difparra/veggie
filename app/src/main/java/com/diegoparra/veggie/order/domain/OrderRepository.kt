package com.diegoparra.veggie.order.domain

import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure

interface OrderRepository {

    suspend fun getMinOrder(): Either<Failure, Int>
    suspend fun getDeliveryBaseCosts(): Either<Failure, DeliveryBaseCosts>
    suspend fun getDeliveryScheduleConfig(): Either<Failure, DeliveryScheduleConfig>

}