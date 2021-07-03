package com.diegoparra.veggie.order.usecases

import com.diegoparra.veggie.auth.domain.AuthRepository
import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.flatMap
import com.diegoparra.veggie.order.domain.Order
import com.diegoparra.veggie.order.domain.OrderRepository
import javax.inject.Inject

class GetOrdersListUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val orderRepository: OrderRepository
) {

    suspend operator fun invoke(userId: String): Either<Failure, List<Order>> {
        return orderRepository.getOrdersForUser(userId)
    }

    suspend fun forCurrentUser(): Either<Failure, List<Order>> {
        return authRepository.getIdCurrentUser().flatMap {
            invoke(userId = it)
        }
    }

}