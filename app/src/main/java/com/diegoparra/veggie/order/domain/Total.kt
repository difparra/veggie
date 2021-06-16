package com.diegoparra.veggie.order.domain

data class Total(
    val subtotal: Int,
    val productsDiscount: Int,
    val additionalDiscounts: Int,      //  Coupons, voucher
    val deliveryCost: Int,
    val tip: Int
) {
    val total = subtotal - productsDiscount - additionalDiscounts + deliveryCost
}