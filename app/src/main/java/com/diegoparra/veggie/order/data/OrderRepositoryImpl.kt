package com.diegoparra.veggie.order.data

import android.content.Context
import com.diegoparra.veggie.core.android.IoDispatcher
import com.diegoparra.veggie.core.android.LocalUpdateHelper
import com.diegoparra.veggie.core.internet.IsInternetAvailableUseCase
import com.diegoparra.veggie.core.kotlin.*
import com.diegoparra.veggie.order.data.DtoToEntityTransformations.toOrderUpdateEntity
import com.diegoparra.veggie.order.data.firebase.OrderApi
import com.diegoparra.veggie.order.data.firebase.OrderConfigApi
import com.diegoparra.veggie.order.data.firebase.order_dto.OrderDtoTransformations.toOrderDto
import com.diegoparra.veggie.order.data.firebase.order_dto.OrderDto
import com.diegoparra.veggie.order.data.room.OrderDao
import com.diegoparra.veggie.order.data.room.OrderEntityTransformations.toOrder
import com.diegoparra.veggie.order.data.room.entities.OrderUpdate
import com.diegoparra.veggie.order.domain.DeliveryBaseCosts
import com.diegoparra.veggie.order.domain.DeliveryScheduleConfig
import com.diegoparra.veggie.order.domain.Order
import com.diegoparra.veggie.order.domain.OrderRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val orderConfigApi: OrderConfigApi,
    private val orderApi: OrderApi,
    private val orderDao: OrderDao,
    isInternetAvailableUseCase: IsInternetAvailableUseCase,
    @ApplicationContext context: Context,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : OrderRepository {


    /*
            CONFIG VALUES   ------------------------------------------------------------------------
     */

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


    /*
            ORDERS   -------------------------------------------------------------------------------
     */

    override suspend fun sendOrder(order: Order): Either<Failure, String> =
        withContext(dispatcher) {
            orderApi.sendOrder(order.toOrderDto())
                .onSuccess {
                    //  Call orders to be updated from server, as soon as some order is sent
                    localUpdateHelper.update(
                        source = Source.SERVER,
                        userId = order.shippingInfo.userId
                    )
                }
        }


    override suspend fun getOrdersForUser(
        userId: String,
        source: Source
    ): Either<Failure, List<Order>> =
        withContext(dispatcher) {
            localUpdateHelper.update(source, userId).onFailure {
                return@withContext Either.Left(it)
            }
            val localOrders = orderDao.getOrdersForUser(userId)
            return@withContext Either.Right(localOrders.map { it.toOrder() })
        }


    /*
            LOCAL UPDATE HELPER   ------------------------------------------------------------------
     */

    private val localUpdateHelper = LocalUpdateHelper(
        lastSuccessfulFetchPrefs = LocalUpdateHelper.TimePrefsImpl(
            key = "orders_updated_at",
            context = context
        ),
        room = orderDao,
        serverApi = orderApi,
        mapper = object : LocalUpdateHelper.Mapper<OrderDto, OrderUpdate> {
            override fun mapToEntity(dto: OrderDto): OrderUpdate = dto.toOrderUpdateEntity()
            override fun isDtoDeleted(dto: OrderDto): Boolean = false
            override fun getId(dto: OrderDto): String = dto.id
        },
        isInternetAvailableUseCase = isInternetAvailableUseCase
    )
}