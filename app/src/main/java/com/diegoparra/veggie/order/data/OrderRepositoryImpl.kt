package com.diegoparra.veggie.order.data

import com.diegoparra.veggie.core.android.IoDispatcher
import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.order.domain.DeliveryBaseCosts
import com.diegoparra.veggie.order.domain.DeliveryScheduleConfig
import com.diegoparra.veggie.order.domain.OrderRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val orderApi: OrderApi,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : OrderRepository {

    override suspend fun getMinOrder(): Either<Failure, Int> = withContext(dispatcher) {
        orderApi.getMinOrder()
    }

    override suspend fun getDeliveryBaseCosts(): Either<Failure, DeliveryBaseCosts> =
        withContext(dispatcher) {
            orderApi.getDeliveryBaseCosts()
        }

    override suspend fun getDeliveryScheduleConfig(): Either<Failure, DeliveryScheduleConfig> =
        withContext(dispatcher) {
            orderApi.getDeliveryScheduleConfig()
        }
}