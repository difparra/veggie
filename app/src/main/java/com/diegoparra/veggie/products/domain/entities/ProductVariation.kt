package com.diegoparra.veggie.products.domain.entities

/*
    Products that will be used in the viewModel / ui layer
 */

open class ProductVariation(
        private val variationData: AdminVariationData
){
    constructor(productVariation: ProductVariation) : this(productVariation.variationData)

    val varId = variationData.varId
    val unit = variationData.unit
    val weightGr = variationData.weightGr
    val details = variationData.detailOptions
    val price = variationData.price
    val discount = variationData.discount
    val stock = variationData.stock
    val maxOrder = variationData.maxOrder
    val label = Label.createLabel(stock, discount, variationData.suggestedLabel)

    fun hasDetails() = !details.isNullOrEmpty()
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