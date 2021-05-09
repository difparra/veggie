package com.diegoparra.veggie.products.domain.entities

/*
    Products that will be used in the viewModel / ui layer
 */

data class ProductVariation(
        private val variation: Variation,
        private val quantitiesByDetail: Map<String?, Int>
) {
    constructor(variation: Variation, quantity: Int) :
            this(variation, mapOf(null to quantity))

    val varId = variation.varId
    val unit = variation.unit
    val weightGr = variation.weightGr
    val details = variation.details
    val price = variation.price
    val discount = variation.discount
    val stock = variation.stock
    val maxOrder = variation.maxOrder
    val label = variation.label

    fun hasDetails() = variation.hasDetails()
    fun quantity(detail: String?) : Int {
        return quantitiesByDetail[detail] ?: 0
    }

}