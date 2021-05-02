package com.diegoparra.veggie.products.domain.entities

import com.diegoparra.veggie.core.Constants

/*
    Products that will be used in the viewModel / ui layer
 */

open class VariationProduct(
        val varId: String,
        val unit: String,
        val weightGr: Int = Constants.Products.NoWeightGr,
        val details: List<String> = listOf(Constants.Products.NoDetail),
        val price: Int,
        val discount: Float,
        val stock: Boolean,
        val maxOrder: Int,
        private val suggestedLabel: String = Constants.Products.NoLabel
){
    constructor(variationProduct: VariationProduct) : this(
            variationProduct.varId, variationProduct.unit, variationProduct.weightGr,
            variationProduct.details, variationProduct.price, variationProduct.discount,
            variationProduct.stock, variationProduct.maxOrder,
            variationProduct.suggestedLabel
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VariationProduct

        if (varId != other.varId) return false
        if (unit != other.unit) return false
        if (weightGr != other.weightGr) return false
        if (details != other.details) return false
        if (price != other.price) return false
        if (discount != other.discount) return false
        if (stock != other.stock) return false
        if (maxOrder != other.maxOrder) return false

        return true
    }

    override fun hashCode(): Int {
        var result = varId.hashCode()
        result = 31 * result + unit.hashCode()
        result = 31 * result + weightGr
        result = 31 * result + details.hashCode()
        result = 31 * result + price
        result = 31 * result + discount.hashCode()
        result = 31 * result + stock.hashCode()
        result = 31 * result + maxOrder
        return result
    }


}

data class VariationProdWithQuantity(
    private val variationProduct: VariationProduct,
    val quantity: Int
) : VariationProduct(variationProduct)