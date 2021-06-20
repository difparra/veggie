package com.diegoparra.veggie.order.data

import com.diegoparra.veggie.core.android.IoDispatcher
import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.combine
import com.diegoparra.veggie.order.domain.DeliveryBaseCosts
import com.diegoparra.veggie.order.domain.DeliveryScheduleConfig
import com.diegoparra.veggie.order.domain.OrderRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val orderConfigApi: OrderConfigApi,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : OrderRepository {

    override suspend fun getMinOrder(): Either<Failure, Int> = withContext(dispatcher) {
        orderConfigApi.getMinOrder()
    }

    override suspend fun getDeliveryBaseCosts(): Either<Failure, DeliveryBaseCosts> =
        withContext(dispatcher) {
            val baseDeliveryCost = orderConfigApi.getDeliveryBaseCost()
            val extraCostOnSameDay = orderConfigApi.getDeliveryExtraCostSameDay()
            Either.combine(baseDeliveryCost, extraCostOnSameDay) { baseCost, extraCost ->
                DeliveryBaseCosts(baseCost, extraCost)
            }
        }

    override suspend fun getDeliveryScheduleConfig(): Either<Failure, DeliveryScheduleConfig> =
        withContext(dispatcher) {
            val deliveryTimetable = orderConfigApi.getDeliveryTimetable()
            val minTimeForDeliveryInHours = orderConfigApi.getMinTimeForDeliveryInHours()
            val maxDaysAhead = orderConfigApi.getMaxDaysForDelivery()
            Either.combine(
                deliveryTimetable,
                minTimeForDeliveryInHours,
                maxDaysAhead
            ) { timetable, minTime, maxDays ->
                DeliveryScheduleConfig(timetable, minTime, maxDays)
            }
        }
}