package com.diegoparra.veggie.order.data

import com.diegoparra.veggie.core.android.IoDispatcher
import com.diegoparra.veggie.core.internet.IsInternetAvailableUseCase
import com.diegoparra.veggie.core.kotlin.*
import com.diegoparra.veggie.order.data.DtoToEntityTransformations.toOrderUpdateEntity
import com.diegoparra.veggie.order.data.firebase.OrderApi
import com.diegoparra.veggie.order.data.firebase.OrderConfigApi
import com.diegoparra.veggie.order.data.firebase.order_dto.OrderDtoTransformations.toOrderDto
import com.diegoparra.veggie.order.data.prefs.OrderPrefs
import com.diegoparra.veggie.order.data.room.OrderDao
import com.diegoparra.veggie.order.data.room.OrderEntityTransformations.toOrder
import com.diegoparra.veggie.order.domain.DeliveryBaseCosts
import com.diegoparra.veggie.order.domain.DeliveryScheduleConfig
import com.diegoparra.veggie.order.domain.Order
import com.diegoparra.veggie.order.domain.OrderRepository
import com.diegoparra.veggie.products.data.toTimestamp
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val orderConfigApi: OrderConfigApi,
    private val orderApi: OrderApi,
    private val orderDao: OrderDao,
    private val orderPrefs: OrderPrefs,
    private val isInternetAvailableUseCase: IsInternetAvailableUseCase,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : OrderRepository {

    override suspend fun getMinOrder(): Either<Failure, Int> = withContext(dispatcher) {
        orderConfigApi.getMinOrder()
    }

    override suspend fun getDeliveryBaseCosts(): Either<Failure, DeliveryBaseCosts> =
        withContext(dispatcher) {
            val baseDeliveryCost = orderConfigApi.getDeliveryBaseCost()
            val extraCostOnSameDay = orderConfigApi.getDeliveryExtraCostSameDay()
            Either.combineMap(baseDeliveryCost, extraCostOnSameDay) { baseCost, extraCost ->
                DeliveryBaseCosts(baseCost, extraCost)
            }
        }

    override suspend fun getDeliveryScheduleConfig(): Either<Failure, DeliveryScheduleConfig> =
        withContext(dispatcher) {
            val deliveryTimetable = orderConfigApi.getDeliveryTimetable()
            val minTimeForDeliveryInHours = orderConfigApi.getMinTimeForDeliveryInHours()
            val maxDaysAhead = orderConfigApi.getMaxDaysForDelivery()
            Either.combineMap(
                deliveryTimetable,
                minTimeForDeliveryInHours,
                maxDaysAhead
            ) { timetable, minTime, maxDays ->
                DeliveryScheduleConfig(timetable, minTime, maxDays)
            }
        }

    override suspend fun sendOrder(order: Order): Either<Failure, String> =
        withContext(dispatcher) {
            orderApi.sendOrder(order.toOrderDto())
                .onSuccess {
                    //  Call orders to be updated from server, as soon as some order is sent
                    updateLocalOrders()
                }
        }


    override suspend fun getOrdersForUser(
        userId: String,
        source: Source
    ): Either<Failure, List<Order>> =
        withContext(dispatcher) {
            updateLocalOrdersIfRequired(source).onFailure {
                return@withContext Either.Left(it)
            }
            val localOrders = orderDao.getOrdersForUser(userId)
            return@withContext Either.Right(localOrders.map { it.toOrder() })
        }


    private suspend fun updateLocalOrdersIfRequired(source: Source): Either<Failure, Unit> {
        val lastSuccessfulFetch = orderPrefs.getOrdersLastSuccessfulFetchAt() ?: BasicTime(0)
        Timber.d("updateLocalOrdersIfRequired called with source = $source, lastSuccessfulFetch = $lastSuccessfulFetch")
        return if (source.mustFetchFromServer(
                lastSuccessfulFetch = lastSuccessfulFetch,
                isInternetAvailable = isInternetAvailableUseCase.invoke().first()
            )
        ) {
            Timber.d("Source says: Data must be collected from server. Updating data...")
            updateLocalOrders()
        } else {
            Timber.d("Source says: Don't collect products from server. Possible reasons: There is no internet access, data is not expired, source was set as cache. Returning without failure...")
            Either.Right(Unit)
        }
    }

    private suspend fun updateLocalOrders(): Either<Failure, Unit> {
        val actualOrdersUpdatedAt = BasicTime(orderDao.getLastUpdatedTime() ?: 0)
        return orderApi.getOrdersUpdatedAfter(actualOrdersUpdatedAt.toTimestamp())
            .map {
                orderDao.updateOrders(it.map { it.toOrderUpdateEntity() })
            }
            .onSuccess {
                Timber.d("Fetch from firebase was successful and orders have been updated locally.")
                orderPrefs.saveOrdersLastSuccessfulFetchAt(BasicTime.now())
            }
    }

}