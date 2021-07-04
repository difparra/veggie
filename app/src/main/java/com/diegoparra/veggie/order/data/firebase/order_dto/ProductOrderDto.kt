package com.diegoparra.veggie.order.data.firebase.order_dto

data class ProductOrderDto(
    val productId: ProductOrderIdDto,
    val name: String,
    val packet: String,
    val weight: Int,
    val unit: String,
    val price: Int,
    val discount: Float,
    val quantity: Int
) {
    //  Required empty constructor for firebase
    constructor(): this(
        productId = ProductOrderIdDto(),
        name = "",
        packet = "",
        weight = -1,
        unit = "",
        price = -1,
        discount = 0.0f,
        quantity = 0
    )
}

data class ProductOrderIdDto(
    val mainId: String,
    val varId: String,
    val detail: String?
) {
    //  Required empty constructor for firebase
    constructor(): this(
        mainId = "",
        varId = "",
        detail = null
    )
}