package com.diegoparra.veggie.order.data.room.entities

import androidx.room.Embedded
import androidx.room.Relation

data class OrderEntity(
    @Embedded val orderDetails: OrderDetailsEntity,
    @Relation(parentColumn = "id", entityColumn = "orderId")
    val productOrder: List<ProductOrderEntity>
)

data class OrderUpdate(
    val orderDetails: OrderDetailsEntity,
    val productsOrder: List<ProductOrderEntity>
)