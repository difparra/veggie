package com.diegoparra.veggie.order.data.room.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "OrderDetails")
data class OrderDetailsEntity (
    @PrimaryKey val id: String,
    @Embedded(prefix = "shipping_") val shippingInfo: ShippingInfoRoomHelper,
    @Embedded(prefix = "total_") val total: TotalRoomHelper,
    @Embedded(prefix = "payment_") val paymentInfo: PaymentInfoRoomHelper,
    val additionalNotes: String?,
    val status: String,
    val updatedAtInMillis: Long
)





/*
        HELPERS OR EMBEDDED FIELDS
 */

data class ShippingInfoRoomHelper(
    val userId: String,
    val phoneNumber: String,
    @Embedded(prefix = "address_") val address: AddressEntity,
    val deliveryFromInMillis: Long,
    val deliveryToInMillis: Long,
    val deliveryCost: Int
)

data class TotalRoomHelper (
    val subtotal: Int,
    val additionalDiscounts: Int,
    val deliveryCost: Int,
    val tip: Int,
    val total: Int
)

data class PaymentInfoRoomHelper(
    val status: String,
    val paymentMethod: String,
    val details: String,
    val additionalInfo: Map<String, String>,
    val updatedAtMillis: Long
)