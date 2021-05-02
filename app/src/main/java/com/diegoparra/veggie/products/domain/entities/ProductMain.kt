package com.diegoparra.veggie.products.domain.entities

import com.diegoparra.veggie.core.Constants

/**
    - Entities that are really part of the client app.
    - Information that could be fetched from the database as a client and used throughout the app.
 */

open class MainProduct(
    val mainId: String,
    val name: String,
    val imageUrl: String,
    val unit: String,
    val weightGr: Int,
    val price: Int,
    val discount: Float,
    val stock: Boolean,
    private val suggestedLabel: String
){
    constructor(mainProduct: MainProduct) : this(
            mainProduct.mainId, mainProduct.name, mainProduct.imageUrl,
            mainProduct.unit, mainProduct.weightGr, mainProduct.price, mainProduct.discount,
            mainProduct.stock, mainProduct.suggestedLabel
    )

    val label: Label
        get() = Label.createLabel(stock, discount, suggestedLabel)

    val description: Description
        get() = Description.createDescription(price, discount, unit, weightGr)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MainProduct

        if (mainId != other.mainId) return false
        if (name != other.name) return false
        if (imageUrl != other.imageUrl) return false
        if (unit != other.unit) return false
        if (weightGr != other.weightGr) return false
        if (price != other.price) return false
        if (discount != other.discount) return false
        if (stock != other.stock) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mainId.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + imageUrl.hashCode()
        result = 31 * result + unit.hashCode()
        result = 31 * result + weightGr
        result = 31 * result + price
        result = 31 * result + discount.hashCode()
        result = 31 * result + stock.hashCode()
        return result
    }

}


data class MainProdWithQuantity(
        val mainProduct: MainProduct,
        val quantity: Int
) : MainProduct(mainProduct)