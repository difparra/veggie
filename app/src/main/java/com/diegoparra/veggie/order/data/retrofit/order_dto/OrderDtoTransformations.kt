package com.diegoparra.veggie.order.data.retrofit.order_dto

import com.diegoparra.veggie.core.kotlin.BasicTime
import com.diegoparra.veggie.order.domain.*
import com.diegoparra.veggie.products.cart.domain.ProductId
import com.diegoparra.veggie.products.data.toBasicTime
import com.diegoparra.veggie.products.data.toTimestamp
import com.diegoparra.veggie.user.data.DtosTransformations.toAddress
import com.diegoparra.veggie.user.data.DtosTransformations.toAddressDto
import com.google.firebase.Timestamp
import java.time.LocalDateTime

object OrderDtoTransformations {

    /*
        --------------------------------------------------------------------------------------------
            TRANSFORMATIONS FROM DOMAIN TO DTO
        --------------------------------------------------------------------------------------------
     */

    fun Order.toOrderDto() = OrderDto(
        id = id,    //  Actually, this value will not be taken into account by the server
        shippingInfo = shippingInfo.toShippingInfoDto(),
        products = products.products.map { it.toProductOrderDto() },
        total = total.toTotalDto(),
        paymentInfo = paymentInfo.toPaymentInfoDto(),
        additionalNotes = additionalNotes,
        status = status.toString(),
        updatedAt = BasicTime.now().millisEpochUTC
    )

    private fun ShippingInfo.toShippingInfoDto() = ShippingInfoDto(
        userId = userId,
        phoneNumber = phoneNumber,
        address = address.toAddressDto(),
        deliverySchedule = deliverySchedule.toDeliveryScheduleDto(),
        deliveryCost = deliveryCost
    )

    private fun DeliverySchedule.toDeliveryScheduleDto() = DeliveryScheduleDto(
        from = LocalDateTime.of(this.date, this.timeRange.from).toBasicTime().millisEpochUTC,
        to = LocalDateTime.of(this.date, this.timeRange.to).toBasicTime().millisEpochUTC
    )

    private fun ProductOrder.toProductOrderDto() = ProductOrderDto(
        productId.toProductOrderIdDto(), name, packet, weight, unit, price, discount, quantity
    )

    private fun ProductId.toProductOrderIdDto() = ProductOrderIdDto(mainId, varId, detail)

    private fun Total.toTotalDto() = TotalDto(
        subtotal,
        additionalDiscounts,
        deliveryCost,
        tip,
        total
    )

    private fun PaymentInfo.toPaymentInfoDto() = PaymentInfoDto(
        status = status.toString(),
        paymentMethod = paymentMethod.toString(),
        details = details,
        additionalInfo = additionalInfo,
        updatedAt = BasicTime.now().millisEpochUTC
    )


    /*
        --------------------------------------------------------------------------------------------
            TRANSFORMATIONS FROM DTO TO DOMAIN
            This was necessary when data was not implemented offline, only from server
        --------------------------------------------------------------------------------------------
     */

    fun OrderDto.toOrder() = Order(
        id = id,
        shippingInfo = shippingInfo.toShippingInfo(),
        products = ProductsList(products.map { it.toProductOrder() }),
        total = total.toTotal(),
        paymentInfo = paymentInfo.toPaymentInfo(),
        additionalNotes = additionalNotes,
        status = Order.Status.valueOf(status),
        updatedAt = BasicTime(updatedAt)
    )

    private fun ShippingInfoDto.toShippingInfo() = ShippingInfo(
        userId = userId,
        phoneNumber = phoneNumber,
        address = address.toAddress(),
        deliverySchedule = deliverySchedule.toDeliverySchedule(),
        deliveryCost = deliveryCost
    )

    private fun DeliveryScheduleDto.toDeliverySchedule() = DeliverySchedule(
        date = BasicTime(this.from).toLocalDateTime().toLocalDate(),
        timeRange = TimeRange(
            from = BasicTime(this.from).toLocalDateTime().toLocalTime(),
            to = BasicTime(this.to).toLocalDateTime().toLocalTime()
        )
    )

    private fun ProductOrderDto.toProductOrder() = ProductOrder(
        productId.toProductId(), name, packet, weight, unit, price, discount, quantity
    )

    private fun ProductOrderIdDto.toProductId() = ProductId(mainId, varId, detail)

    private fun TotalDto.toTotal() = Total(
        subtotal,
        additionalDiscounts,
        deliveryCost,
        tip
    )

    private fun PaymentInfoDto.toPaymentInfo() = PaymentInfo(
        status = PaymentStatus.valueOf(status),
        paymentMethod = PaymentMethod.valueOf(paymentMethod),
        details = details,
        additionalInfo = additionalInfo ?: mapOf()
    )

}