package com.diegoparra.veggie.products.domain.entities

/**
    - Entities that are really part of the client app.
    - Information that could be fetched from the database as a client and used throughout the app.
 */

data class MainProduct(
    val mainId: String,
    val name: String,
    val imageUrl: String,
    val unit: String,
    val weightGr: Int,
    val price: Int,
    val discount: Float,
    val stock: Boolean,
    val suggestedLabel: String? = null
)

data class VariationProduct(
    val varId: String,
    val unit: String,
    val weightGr: Int = ConstantsProducts.NoWeightGr,
    val details: List<String> = listOf(ConstantsProducts.NoDetail),
    val price: Int,
    val discount: Float,
    val stock: Boolean,
    val maxOrder: Int,
    val suggestedLabel: String? = null
)