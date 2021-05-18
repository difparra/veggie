package com.diegoparra.veggie.products.domain

/**
 * Information to be fetched from the database in the client app.
 */

data class Product(
        //  AdminMainData filtered to what is needed in the client app
    val mainId: String,
    val name: String,
    val imageUrl: String,
        //  variationData filtered to what is needed in the client app
    val variation: Variation,
){
    val varId: String = variation.varId
    val unit: String = variation.unit
    val weightGr: Int = variation.weightGr
    val price: Int = variation.price
    val discount: Float = variation.discount
    val stock: Boolean = variation.stock
    val maxOrder: Int = variation.maxOrder
    val label: Label = variation.label
}

data class Variation(
        val mainId: String,
        val varId: String,
        val unit: String,
        val weightGr: Int,
        val details: List<String>?,
        val price: Int,
        val discount: Float,
        val stock: Boolean,
        val maxOrder: Int,
        private val suggestedLabel: String?
){
    val label = Label.createLabel(stock, discount, suggestedLabel)
    fun hasDetails() = !details.isNullOrEmpty()
}