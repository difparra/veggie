package com.diegoparra.veggie.order.domain

data class Total(
    val subtotal: Int,
    val additionalDiscounts: Int,      //  Coupons, voucher
    val deliveryCost: Int,
    val tip: Int
) {
    val total = subtotal - additionalDiscounts + deliveryCost + tip
    operator fun invoke() = total
}