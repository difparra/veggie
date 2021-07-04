package com.diegoparra.veggie.order.data.room.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "ProductsOrder",
    foreignKeys = [
        ForeignKey(entity = OrderDetailsEntity::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE)
    ],
    primaryKeys = ["orderId", "mainId", "variationId", "detail"],
)
data class ProductOrderEntity(
    val orderId: String,
    val mainId: String,
    val variationId: String,
    val detail: String,
    val name: String,
    val packet: String,
    val weight: Int,
    val unit: String,
    val price: Int,
    val discount: Float,
    val quantity: Int
)