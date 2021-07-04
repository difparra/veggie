package com.diegoparra.veggie.order.data

import com.diegoparra.veggie.order.data.firebase.order_dto.*
import com.diegoparra.veggie.order.data.room.entities.*
import com.diegoparra.veggie.products.data.toBasicTime
import com.diegoparra.veggie.user.data.firebase.AddressDto

object DtoToEntityTransformations {

    fun OrderDto.toOrderUpdateEntity(orderId: String) = OrderUpdate(
        orderDetails = this.toOrderDetailsEntity(orderId),
        productsOrder = this.products.map { it.toProductOrderEntity(orderId) }
    )

    private fun OrderDto.toOrderDetailsEntity(orderId: String) = OrderDetailsEntity(
        id = orderId,
        shippingInfo = shippingInfo.toShippingInfoRoomHelper(),
        total = total.toTotalRoomHelper(),
        paymentInfo = paymentInfo.toPaymentInfoRoomHelper(),
        additionalNotes = additionalNotes,
        status = status,
        updatedAtInMillis = updatedAt.toBasicTime().millisEpochUTC
    )

    private fun ShippingInfoDto.toShippingInfoRoomHelper() = ShippingInfoRoomHelper(
        userId = userId,
        phoneNumber = phoneNumber,
        address = address.toAddressEntity(),
        deliveryFromInMillis = deliverySchedule.from.toBasicTime().millisEpochUTC,
        deliveryToInMillis = deliverySchedule.to.toBasicTime().millisEpochUTC,
        deliveryCost = deliveryCost
    )

    private fun AddressDto.toAddressEntity() = AddressEntity(
        id = id,
        address = address,
        details = details,
        instructions = instructions
    )

    private fun TotalDto.toTotalRoomHelper() = TotalRoomHelper(
        subtotal, additionalDiscounts, deliveryCost, tip, total
    )

    private fun PaymentInfoDto.toPaymentInfoRoomHelper() = PaymentInfoRoomHelper(
        status = status,
        paymentMethod = paymentMethod,
        details = details,
        additionalInfo = additionalInfo,
        updatedAtMillis = updatedAt.toBasicTime().millisEpochUTC
    )

    private fun ProductOrderDto.toProductOrderEntity(orderId: String) = ProductOrderEntity(
        orderId = orderId,
        mainId = productId.mainId,
        variationId = productId.varId,
        detail = productId.detail ?: "",
        name = name,
        packet = packet,
        weight = weight,
        unit = unit,
        price = price,
        discount = discount,
        quantity = quantity
    )

}