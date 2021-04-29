package com.diegoparra.veggie.products.domain.entities

data class CartItem(
    val productId: ProductId,
    val quantity: Int
)