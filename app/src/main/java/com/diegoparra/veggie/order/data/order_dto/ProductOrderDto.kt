package com.diegoparra.veggie.order.data.order_dto

import com.diegoparra.veggie.products.cart.domain.ProductId

data class ProductOrderDto(
    val productId: ProductId,
    val name: String,
    val packet: String,
    val weight: Int,
    val unit: String,
    val price: Int,
    val discount: Float,
    val quantity: Int
)

data class ProductOrderIdDto(
    val mainId: String,
    val varId: String,
    val detail: String
)