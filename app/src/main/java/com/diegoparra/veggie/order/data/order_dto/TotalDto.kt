package com.diegoparra.veggie.order.data.order_dto

data class TotalDto(
    val productsBeforeDiscount: Int,
    val productsDiscount: Int,
    val subtotal: Int,
    val additionalDiscounts: Int,
    val deliveryCost: Int,
    val tip: Int,
    val total: Int
)