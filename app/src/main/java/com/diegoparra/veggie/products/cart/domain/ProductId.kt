package com.diegoparra.veggie.products.cart.domain

data class ProductId(
    val mainId: String,
    val varId: String,
    val detail: String? = null
)