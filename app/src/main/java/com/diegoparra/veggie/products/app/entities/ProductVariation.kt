package com.diegoparra.veggie.products.app.entities

import com.diegoparra.veggie.products.domain.VariationData

data class ProductVariation(
    private val variation: VariationData,
    private val quantitiesByDetail: Map<String?, Int>
) {
    constructor(variation: VariationData, quantity: Int) :
            this(variation, mapOf(null to quantity))

    val varId = variation.varId
    val packet = variation.packet
    val weight = variation.weight
    val unit = variation.unit
    val details = variation.detailOptions
    val price = variation.price
    val discount = variation.discount
    val stock = variation.stock
    val maxOrder = variation.maxOrder
    val label = variation.label

    fun hasDetails() = variation.hasDetails()
    fun quantity(detail: String?): Int {
        return quantitiesByDetail[detail] ?: 0
    }

}