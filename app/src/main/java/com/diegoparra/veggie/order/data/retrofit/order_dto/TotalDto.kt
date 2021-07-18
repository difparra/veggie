package com.diegoparra.veggie.order.data.retrofit.order_dto

data class TotalDto(
    val subtotal: Int,
    val additionalDiscounts: Int,
    val deliveryCost: Int,
    val tip: Int,
    val total: Int
)