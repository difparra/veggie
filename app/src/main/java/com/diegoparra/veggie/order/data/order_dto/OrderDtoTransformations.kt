package com.diegoparra.veggie.order.data.order_dto

import com.diegoparra.veggie.order.domain.*
import com.diegoparra.veggie.user.data.DtosTransformations.toAddressDto
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import java.time.LocalDateTime

object OrderDtoTransformations {

    fun Order.toOrderDto() = OrderDto(
        shippingInfo = shippingInfo.toShippingInfoDto(),
        products = products.products.map { it.toProductOrderDto() },
        total = total.toTotalDto(),
        paymentInfo = paymentInfo.toPaymentInfoDto(),
        additionalNotes = additionalNotes,
        status = status.toString(),
        updatedAt = updatedAtMillis.toTimestamp()
    )

    private fun ShippingInfo.toShippingInfoDto() = ShippingInfoDto(
        userId = userId,
        phoneNumber = phoneNumber,
        address = address.toAddressDto(),
        deliverySchedule = deliverySchedule.toDeliveryScheduleDto(),
        deliveryCost = deliveryCost
    )

    private fun DeliverySchedule.toDeliveryScheduleDto() = DeliveryScheduleDto(
        from = LocalDateTime.of(this.date, this.timeRange.from).toTimestamp(),
        to = LocalDateTime.of(this.date, this.timeRange.to).toTimestamp()
    )

    private fun ProductOrder.toProductOrderDto() = ProductOrderDto(
        productId, name, packet, weight, unit, price, discount, quantity
    )

    private fun Total.toTotalDto() = TotalDto(
        productsBeforeDiscount, productsDiscount, subtotal, additionalDiscounts, deliveryCost, tip, total
    )

    private fun PaymentInfo.toPaymentInfoDto() = PaymentInfoDto(
        status = status.toString(),
        paymentMethod = paymentMethod.toPaymentMethodDto(),
        total = total,
        paidAt = paidAt.toTimestamp()
    )

    private fun PaymentMethod.toPaymentMethodDto() =
        when(this) {
            is PaymentMethod.Cash -> PaymentMethodDto(method = "cash", additionalInfo = mapOf("payWith" to payWith.toString()))
            is PaymentMethod.Pos -> PaymentMethodDto(method = "pos", additionalInfo = mapOf("ticket" to ticket))
            is PaymentMethod.Card -> PaymentMethodDto(method = "card", additionalInfo = mapOf())
        }


    private fun Long.toTimestamp() = Timestamp(this/1000, 0)
    private fun LocalDateTime.toTimestamp() = Timestamp(this.second.toLong(), this.nano)

}