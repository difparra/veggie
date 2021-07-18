package com.diegoparra.veggie.order.data.room

import com.diegoparra.veggie.core.kotlin.BasicTime
import com.diegoparra.veggie.core.toLocalDateTime
import com.diegoparra.veggie.order.data.room.entities.*
import com.diegoparra.veggie.order.domain.*
import com.diegoparra.veggie.products.cart.domain.ProductId
import com.diegoparra.veggie.user.address.domain.Address

object OrderEntityTransformations {

    /*
        --------------------------------------------------------------------------------------------
            TRANSFORMATIONS FROM ENTITY TO DOMAIN
        --------------------------------------------------------------------------------------------
     */

    fun OrderEntity.toOrder() = Order(
        id = orderDetails.id,
        shippingInfo = orderDetails.shippingInfo.toShippingInfo(),
        products = ProductsList(productOrder.map { it.toProductOrder() }),
        total = orderDetails.total.toTotal(),
        paymentInfo = orderDetails.paymentInfo.toPaymentInfo(),
        additionalNotes = orderDetails.additionalNotes,
        status = Order.Status.valueOf(orderDetails.status),
        updatedAt = BasicTime(orderDetails.updatedAtInMillis)
    )

    private fun ShippingInfoRoomHelper.toShippingInfo() = ShippingInfo(
        userId = userId,
        phoneNumber = phoneNumber,
        address = address.toAddress(),
        deliverySchedule = this.toDeliverySchedule(),
        deliveryCost = deliveryCost
    )

    private fun AddressEntity.toAddress() = Address(
        id = id,
        address = address,
        details = details,
        instructions = instructions
    )

    private fun ShippingInfoRoomHelper.toDeliverySchedule() = DeliverySchedule(
        date = BasicTime(this.deliveryFromInMillis).toLocalDateTime().toLocalDate(),
        timeRange = TimeRange(
            from = BasicTime(this.deliveryFromInMillis).toLocalDateTime().toLocalTime(),
            to = BasicTime(this.deliveryToInMillis).toLocalDateTime().toLocalTime()
        )
    )

    private fun ProductOrderEntity.toProductOrder() = ProductOrder(
        productId = ProductId(mainId, variationId, if (detail.isNotEmpty()) detail else null),
        name = name,
        packet = packet,
        weight = weight,
        unit = unit,
        price = price,
        discount = discount,
        quantity = quantity
    )

    private fun TotalRoomHelper.toTotal() = Total(
        subtotal, additionalDiscounts, deliveryCost, tip
    )

    private fun PaymentInfoRoomHelper.toPaymentInfo() = PaymentInfo(
        status = PaymentStatus.valueOf(status),
        paymentMethod = PaymentMethod.valueOf(paymentMethod),
        details = details,
        additionalInfo = additionalInfo
    )

}