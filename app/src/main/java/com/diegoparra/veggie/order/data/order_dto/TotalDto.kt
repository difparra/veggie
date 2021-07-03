package com.diegoparra.veggie.order.data.order_dto

data class TotalDto(
    val subtotal: Int,
    val additionalDiscounts: Int,
    val deliveryCost: Int,
    val tip: Int,
    val total: Int
){
    //  Required empty constructor for firebase
    constructor(): this(
        subtotal = -1,
        additionalDiscounts = -1,
        deliveryCost = -1,
        tip = -1,
        total = -1
    )
}