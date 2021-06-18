package com.diegoparra.veggie.order.domain

import com.diegoparra.veggie.core.kotlin.getValueBeforeDiscount
import com.diegoparra.veggie.products.cart.domain.ProductId

@JvmInline
value class ProductsList(val products: List<ProductOrder>) {
    fun subtotal() = products.sumOf { it.total }
    fun totalBeforeDiscounts() = products.sumOf { it.priceBeforeDiscount }
    fun totalDiscounts() = products.sumOf { (it.price * it.discount).toInt() }
}

data class ProductOrder(
    val productId: ProductId,
    val name: String,
    val unit: String,
    val weight: Int,
    val price: Int,
    val discount: Float,
    val quantity: Int
) {
    val priceBeforeDiscount = price.getValueBeforeDiscount(discount)
    val total = price * quantity
}