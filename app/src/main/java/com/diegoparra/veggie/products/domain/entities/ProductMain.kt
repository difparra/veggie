package com.diegoparra.veggie.products.domain.entities

/**
    - Entities that are really part of the client app.
    - Information that could be fetched from the database as a client and used throughout the app.
 */

open class MainProduct(
        private val mainData: AdminMainData,
        private val mainVariationData: AdminVariationData,
) {
    constructor(mainProduct: MainProduct) : this(mainProduct.mainData, mainProduct.mainVariationData)

    val mainId = mainData.mainId
    val name = mainData.name
    val imageUrl = mainData.imageUrl
    val unit = mainVariationData.unit
    val weightGr = mainVariationData.weightGr
    val price = mainVariationData.price
    val discount = mainVariationData.discount
    val stock = mainVariationData.stock
    val label = Label.createLabel(stock, discount, mainVariationData.suggestedLabel)
    val description = Description(price, discount, unit, weightGr)
}

data class MainProdWithQuantity(
        val mainProduct: MainProduct,
        val quantity: Int
) : MainProduct(mainProduct)