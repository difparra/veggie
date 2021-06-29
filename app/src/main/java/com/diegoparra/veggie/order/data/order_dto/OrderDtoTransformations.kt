package com.diegoparra.veggie.order.data.order_dto

import com.diegoparra.veggie.order.domain.*
import com.diegoparra.veggie.products.cart.domain.ProductId
import com.diegoparra.veggie.products.data.toBasicTime
import com.diegoparra.veggie.products.data.toTimestamp
import com.diegoparra.veggie.user.data.DtosTransformations.toAddress
import com.diegoparra.veggie.user.data.DtosTransformations.toAddressDto
import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.time.ZoneOffset

object OrderDtoTransformations {

    //  Payment method constants
    private const val cashPaymentMethod = "cash"
    private const val posPaymentMethod = "pos"
    private const val cardPaymentMethod = "card"

    //  Additional info payment method constants
    private const val keyCashPayWith = "payWith"
    private const val keyPosTicket = "ticket"

    /*
        --------------------------------------------------------------------------------------------
            TRANSFORMATIONS FROM DOMAIN TO DTO
        --------------------------------------------------------------------------------------------
     */

    fun Order.toOrderDto() = OrderDto(
        shippingInfo = shippingInfo.toShippingInfoDto(),
        products = products.products.map { it.toProductOrderDto() },
        total = total.toTotalDto(),
        paymentInfo = paymentInfo.toPaymentInfoDto(),
        additionalNotes = additionalNotes,
        status = status.toString(),
        updatedAt = updatedAt.toTimestamp()
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
        productId.toProductOrderIdDto(), name, packet, weight, unit, price, discount, quantity
    )

    private fun ProductId.toProductOrderIdDto() = ProductOrderIdDto(mainId, varId, detail)

    private fun Total.toTotalDto() = TotalDto(
        productsBeforeDiscount,
        productsDiscount,
        subtotal,
        additionalDiscounts,
        deliveryCost,
        tip,
        total
    )

    private fun PaymentInfo.toPaymentInfoDto() = PaymentInfoDto(
        status = status.toString(),
        paymentMethod = paymentMethod.toPaymentMethodDto(),
        total = total,
        paidAt = paidAt?.toTimestamp()
    )

    private fun PaymentMethod.toPaymentMethodDto() =
        when (this) {
            is PaymentMethod.Cash -> PaymentMethodDto(
                method = cashPaymentMethod, additionalInfo = mapOf(
                    keyCashPayWith to payWith.toString()
                )
            )
            is PaymentMethod.Pos -> PaymentMethodDto(
                method = posPaymentMethod, additionalInfo = mapOf(
                    keyPosTicket to ticket
                )
            )
            is PaymentMethod.Card -> PaymentMethodDto(
                method = cardPaymentMethod,
                additionalInfo = mapOf()
            )
        }


    /*
        --------------------------------------------------------------------------------------------
            TRANSFORMATIONS FROM DTO TO DOMAIN
        --------------------------------------------------------------------------------------------
     */

    fun OrderDto.toOrder(orderId: String) = Order(
        id = orderId,
        shippingInfo = shippingInfo.toShippingInfo(),
        products = ProductsList(products.map { it.toProductOrder() }),
        total = total.toTotal(),
        paymentInfo = paymentInfo.toPaymentInfo(),
        additionalNotes = additionalNotes,
        status = Order.Status.valueOf(status),
        updatedAt = updatedAt.toBasicTime()
    )

    private fun ShippingInfoDto.toShippingInfo() = ShippingInfo(
        userId = userId,
        phoneNumber = phoneNumber,
        address = address.toAddress(),
        deliverySchedule = deliverySchedule.toDeliverySchedule(),
        deliveryCost = deliveryCost
    )

    private fun DeliveryScheduleDto.toDeliverySchedule() = DeliverySchedule(
        date = this.from.toLocalDateTime().toLocalDate(),
        timeRange = TimeRange(
            from = this.from.toLocalDateTime().toLocalTime(),
            to = this.to.toLocalDateTime().toLocalTime()
        )
    )

    private fun ProductOrderDto.toProductOrder() = ProductOrder(
        productId.toProductId(), name, packet, weight, unit, price, discount, quantity
    )

    private fun ProductOrderIdDto.toProductId() = ProductId(mainId, varId, detail)

    private fun TotalDto.toTotal() = Total(
        productsBeforeDiscount,
        productsDiscount,
        subtotal,
        additionalDiscounts,
        deliveryCost,
        tip
    )

    private fun PaymentInfoDto.toPaymentInfo() = PaymentInfo(
        status = PaymentStatus.valueOf(status),
        paymentMethod = paymentMethod.toPaymentMethod(),
        total = total,
        paidAt = paidAt?.toBasicTime()
    )

    private fun PaymentMethodDto.toPaymentMethod(): PaymentMethod {
        return when (this.method) {
            cashPaymentMethod -> PaymentMethod.Cash(payWith = additionalInfo[keyCashPayWith])
            posPaymentMethod -> PaymentMethod.Pos(ticket = additionalInfo[keyPosTicket] ?: "")
            cardPaymentMethod -> PaymentMethod.Card()
            else -> throw IllegalArgumentException("methodString saved in database is not valid")
        }
    }

}