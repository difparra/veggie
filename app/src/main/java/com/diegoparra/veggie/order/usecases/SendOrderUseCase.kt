package com.diegoparra.veggie.order.usecases

import com.diegoparra.veggie.core.kotlin.BasicTime
import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.order.domain.*
import com.diegoparra.veggie.products.cart.domain.CartRepository
import javax.inject.Inject

class SendOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
    private val cartRepository: CartRepository
) {

    suspend operator fun invoke(
        shippingInfo: ShippingInfo, products: ProductsList, total: Total,
        paymentInfo: PaymentInfo, additionalNotes: String? = null
    ): Either<Failure, String> {
        //  Cash and pos will be payment against delivery
        val orderStatus =
            if (paymentInfo.paymentMethod == PaymentMethod.CASH || paymentInfo.paymentMethod == PaymentMethod.POS) {
                Order.Status.CREATED
            } else {
                Order.Status.STARTED
            }
        //  Creating order
        val order = Order(
            id = "",        //  Created in the repository
            shippingInfo = shippingInfo,
            products = products,
            total = total,
            paymentInfo = paymentInfo,
            additionalNotes = additionalNotes,
            status = orderStatus,
            updatedAt = BasicTime.now()
        )
        //  Sending order
        val result = orderRepository.sendOrder(order)
        //  If needed, here is the place to check payment
        //  And then, update the payment info in the already created order in database
        if (result is Either.Right) {
            clearCartList()
        }
        return result
    }

    private suspend fun clearCartList() {
        cartRepository.deleteAllItems()
    }
}