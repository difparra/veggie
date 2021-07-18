package com.diegoparra.veggie.order.data.retrofit.order_dto

data class ProductOrderDto(
    val productId: ProductOrderIdDto,
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
    val detail: String?
)