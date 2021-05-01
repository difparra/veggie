package com.diegoparra.veggie.products.domain.entities

import com.diegoparra.veggie.core.Constants

data class ProductId(
    val mainId: String,
    val varId: String,
    val detail: String = Constants.Products.NoDetail
)