package com.diegoparra.veggie.order.usecases

import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.Source
import com.diegoparra.veggie.core.kotlin.flatMap
import com.diegoparra.veggie.order.domain.Order
import com.diegoparra.veggie.order.domain.OrderRepository
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class GetOrdersListUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val orderRepository: OrderRepository
) {

    suspend operator fun invoke(
        userId: String,
        isInternetAvailable: Boolean
    ): Either<Failure, List<Order>> {
        val source = Source.getDefaultSourceForInternetAccessState(
            isInternetAvailable,
            TimeUnit.MINUTES.toMillis(5)
        )
        return orderRepository.getOrdersForUser(userId, source)
    }

    suspend fun forCurrentUser(isInternetAvailable: Boolean): Either<Failure, List<Order>> {
        return authRepository.getIdCurrentUser().flatMap {
            invoke(userId = it, isInternetAvailable = isInternetAvailable)
        }
    }

}