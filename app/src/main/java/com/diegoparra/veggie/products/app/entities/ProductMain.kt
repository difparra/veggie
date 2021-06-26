package com.diegoparra.veggie.products.app.entities

import com.diegoparra.veggie.products.domain.Product

/**
 * Classes with prefix Product are normally products ready to be used in the ui.
 */

data class ProductMain(
    private val product: Product,
    val quantity: Int
) {
    val mainId = product.mainData.mainId
    val name = product.mainData.name
    val imageUrl = product.mainData.imageUrl
    val packet = product.variationData.packet
    val weight = product.variationData.weight
    val unit = product.variationData.unit
    val price = product.variationData.price
    val discount = product.variationData.discount
    val stock = product.variationData.stock
    val label = product.variationData.label

}