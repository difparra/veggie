package com.diegoparra.veggie.order.data

import com.diegoparra.veggie.order.data.retrofit.order_dto.*
import com.diegoparra.veggie.order.data.room.entities.*
import com.diegoparra.veggie.user.data.firebase.AddressDto

object DtoToEntityTransformationsRetrofit {

    fun OrderDto.toOrderUpdateEntity() = OrderUpdate(
        orderDetails = this.toOrderDetailsEntity(),
        productsOrder = this.products.map { it.toProductOrderEntity(this.id) }
    )

    private fun OrderDto.toOrderDetailsEntity() = OrderDetailsEntity(
        id = id,
        shippingInfo = shippingInfo.toShippingInfoRoomHelper(),
        total = total.toTotalRoomHelper(),
        paymentInfo = paymentInfo.toPaymentInfoRoomHelper(),
        additionalNotes = additionalNotes,
        status = status,
        updatedAtInMillis = updatedAt
    )

    private fun ShippingInfoDto.toShippingInfoRoomHelper() = ShippingInfoRoomHelper(
        userId = userId,
        phoneNumber = phoneNumber,
        address = address.toAddressEntity(),
        deliveryFromInMillis = deliverySchedule.from,
        deliveryToInMillis = deliverySchedule.to,
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
        additionalInfo = additionalInfo ?: mapOf(),
        updatedAtMillis = updatedAt
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