package com.diegoparra.veggie.products.domain.entities

/*
    Products that will be used in the viewModel / ui layer
 */

open class ProductVariation(
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
    constructor(productVariation: ProductVariation) : this(
            productVariation.varId, productVariation.unit, productVariation.weightGr,
            productVariation.details, productVariation.price, productVariation.discount,
            productVariation.stock, productVariation.maxOrder,
            productVariation.suggestedLabel
    )

    val hasDetails = !details.isNullOrEmpty()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProductVariation

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

data class ProdVariationWithQuantities(
        private val productVariation: ProductVariation,
        private val quantitiesByDetail: Map<String?, Int>
) : ProductVariation(productVariation) {

    constructor(productVariation: ProductVariation, quantity: Int) : this(
            productVariation, mapOf(null to quantity))

    fun quantity(detail: String?) : Int {
        return quantitiesByDetail[detail] ?: 0
    }

}