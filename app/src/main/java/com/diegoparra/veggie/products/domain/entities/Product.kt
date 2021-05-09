package com.diegoparra.veggie.products.domain.entities

/*
    Information fetched from database in client app
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
    val label = variation.label
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