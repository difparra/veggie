package com.diegoparra.veggie.order.usecases

import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.order.domain.*
import com.diegoparra.veggie.products.cart.domain.CartRepository
import java.time.LocalDateTime
import javax.inject.Inject

class SendOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
    private val cartRepository: CartRepository
) {

    suspend operator fun invoke(
        shippingInfo: ShippingInfo, products: ProductsList, total: Total,
        paymentInfo: PaymentInfo, additionalNotes: String? = null
    ): Either<Failure, String> {
        val order = Order(
            id = "",        //  Created in the repository
            shippingInfo = shippingInfo,
            products = products,
            total = total,
            paymentInfo = paymentInfo,
            additionalNotes = additionalNotes,
            status = Order.Status.CREATED,
            updatedAtMillis = System.currentTimeMillis()
        )
        val result = orderRepository.sendOrder(order)
        if (result is Either.Right) {
            clearCartList()
        }
        return result
    }

    fun createPaymentInfo(
        paymentStatus: PaymentStatus, paymentMethod: PaymentMethod,
        total: Double, paidAt: Long?
    ): PaymentInfo = PaymentInfo(
        status = paymentStatus,
        paymentMethod = paymentMethod,
        total = total,
        paidAt = paidAt ?: 0L
    )


    private suspend fun clearCartList() {
        cartRepository.deleteAllItems()
    }
}