package com.diegoparra.veggie.products.cart.domain

data class CartItem(
    val productId: ProductId,
    val quantity: Int
)