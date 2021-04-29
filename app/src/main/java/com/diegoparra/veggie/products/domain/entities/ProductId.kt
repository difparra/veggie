package com.diegoparra.veggie.products.domain.entities

data class ProductId(
    val mainId: String,
    val varId: String,
    val detail: String = ConstantsProducts.NoDetail
)