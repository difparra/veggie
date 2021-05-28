package com.diegoparra.veggie.products.entities

import com.diegoparra.veggie.products.domain.Product

/**
 * Classes with prefix Product are normally products ready to be used in the ui.
 */

data class ProductMain(
    private val product: Product,
    val quantity: Int
){
    val mainId = product.mainData.mainId
    val name = product.mainData.name
    val imageUrl = product.mainData.imageUrl
    val unit = product.variationData.unit
    val weightGr = product.variationData.weightGr
    val price = product.variationData.price
    val discount = product.variationData.discount
    val stock = product.variationData.stock
    val label = product.variationData.label

}